package net.smolok.service.binding

class ServiceEvent {

    private final String channel

    private final Object body

    private final Map<String, Object> headers

    ServiceEvent(String channel, Object body, Map<String, Object> headers) {
        this.channel = channel
        this.body = body
        this.headers = new LinkedHashMap<>(headers)
    }

    String channel() {
        channel
    }

    Object body() {
        body
    }

    Map<String, Object> headers() {
        new LinkedHashMap<>(headers)
    }

}
