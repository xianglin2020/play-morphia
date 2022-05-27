package it.unifi.cerm.playmorphia;

import com.typesafe.config.Config;
import play.Environment;
import play.inject.Binding;

import java.util.Collections;
import java.util.List;

/**
 * @author morelli
 */
public class MorphiaModule extends play.inject.Module {
    public List<Binding<?>> bindings(Environment environment, Config config) {
        return Collections.singletonList(
                bindClass(PlayMorphia.class).toSelf()
        );
    }


}




