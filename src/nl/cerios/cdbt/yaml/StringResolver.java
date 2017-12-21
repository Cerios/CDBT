package nl.cerios.cdbt.yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.resolver.Resolver;

/**
 * Created by dwhelan on 06/12/2017.
 */
public class StringResolver extends Resolver {

    protected void addImplicitResolvers() {
        //addImplicitResolver(Tag.BOOL, BOOL, "yYnNtTfFoO");
        // addImplicitResolver(Tags.FLOAT, FLOAT, "-+0123456789.");
        //addImplicitResolver(Tag.INT, INT, "-+0123456789");
        addImplicitResolver(Tag.MERGE, MERGE, "<");
        //addImplicitResolver(Tag.NULL, NULL, "~nN\0");
        //addImplicitResolver(Tag.NULL, EMPTY, null);
        // addImplicitResolver(Tags.TIMESTAMP, TIMESTAMP, "0123456789");
    }
}