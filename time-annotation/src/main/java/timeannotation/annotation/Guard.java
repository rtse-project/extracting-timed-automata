package timeannotation.annotation;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.ANNOTATION_TYPE})
public @interface Guard {
	String expr();
}
