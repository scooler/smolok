package smolok.lib.docker

import com.google.common.collect.ImmutableList

/**
 * Represents CommandLineDocker container to be created.
 */
class Container {

    private final String image

    private final String name

    private final String net

    private final String[] arguments

    Container(String image, String name, String net, String[] arguments) {
        this.image = image
        this.name = name
        this.net = net
        this.arguments = arguments
    }

    static Container container(String image, String name) {
        new Container(image, name, null)
    }

    static Container container(String image) {
        new Container(image, null, null)
    }

    // Getters

    String image() {
        image
    }

    String name() {
        name
    }

    String net() {
        net
    }

    List<String> arguments() {
        ImmutableList.copyOf(arguments)
    }

}
