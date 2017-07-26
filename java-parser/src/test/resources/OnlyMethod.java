/**
 * Created by giovanni (@thisthatDC) on 18/03/16.
 */

public void startInitialJoin() {
synchronized (clusterGroups) {
        ClusterGroup clusterGroup = clusterGroups.get(clusterName);
        if (clusterGroup == null) {
        clusterGroup = new ClusterGroup();
        clusterGroups.put(clusterName, clusterGroup);
        }
        logger.debug("Connected to cluster [{}]", clusterName);

        clusterGroup.members().add(this);

        LocalDiscovery firstMaster = null;
        for (LocalDiscovery localDiscovery : clusterGroup.members()) {
        if (localDiscovery.localNode().masterNode()) {
        firstMaster = localDiscovery;
        break;
        }
        }

        if (firstMaster != null && firstMaster.equals(this)) {
        // we are the first master (and the master)
        master = true;
final LocalDiscovery master = firstMaster;
        clusterService.submitStateUpdateTask("local-disco-initial_connect(master)", new ClusterStateUpdateTask() {

@Override
public boolean runOnlyOnMaster() {
        return false;
        }

@Override
public ClusterState execute(ClusterState currentState) {
        DiscoveryNodes.Builder nodesBuilder = DiscoveryNodes.builder();
        for (LocalDiscovery discovery : clusterGroups.get(clusterName).members()) {
        nodesBuilder.put(discovery.localNode());
        }
        nodesBuilder.localNodeId(master.localNode().id()).masterNodeId(master.localNode().id());
        // remove the NO_MASTER block in this case
        ClusterBlocks.Builder blocks = ClusterBlocks.builder().blocks(currentState.blocks()).removeGlobalBlock(discoverySettings.getNoMasterBlock());
        return ClusterState.builder(currentState).nodes(nodesBuilder).blocks(blocks).build();
        }

@Override
public void onFailure(String source, Throwable t) {
        logger.error("unexpected failure during [{}]", t, source);
        }
        });
        } else if (firstMaster != null) {
// tell the master to send the fact that we are here
final LocalDiscovery master = firstMaster;
        firstMaster.clusterService.submitStateUpdateTask("local-disco-receive(from node[" + localNode() + "])", new ClusterStateUpdateTask() {
@Override
public boolean runOnlyOnMaster() {
        return false;
        }

@Override
public ClusterState execute(ClusterState currentState) {
        DiscoveryNodes.Builder nodesBuilder = DiscoveryNodes.builder();
        for (LocalDiscovery discovery : clusterGroups.get(clusterName).members()) {
        nodesBuilder.put(discovery.localNode());
        }
        nodesBuilder.localNodeId(master.localNode().id()).masterNodeId(master.localNode().id());
        return ClusterState.builder(currentState).nodes(nodesBuilder).build();
        }

@Override
public void onFailure(String source, Throwable t) {
        logger.error("unexpected failure during [{}]", t, source);
        }

@Override
public void clusterStateProcessed(String source, ClusterState oldState, ClusterState newState) {
        // we reroute not in the same cluster state update since in certain areas we rely on
        // the node to be in the cluster state (sampled from ClusterService#state) to be there, also
        // shard transitions need to better be handled in such cases
        master.routingService.reroute("post_node_add");
        }
        });
        }
        } // else, no master node, the next node that will start will fill things in...
        }