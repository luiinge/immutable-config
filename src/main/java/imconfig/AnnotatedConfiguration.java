/**
 * @author Luis Iñesta Gelabert - linesta@iti.es | luiinge@gmail.com
 */
package imconfig;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface AnnotatedConfiguration {

    String path() default "";


    String pathPrefix() default "";


    Property[] properties() default {};


    boolean systemProperties() default false;


    boolean environmentProperties() default false;


}
