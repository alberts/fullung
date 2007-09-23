package net.lunglet.concurrent;

public final class UppercaseStringTask extends JMSXGroupTask<String> {
    private static final long serialVersionUID = 1L;

    private final String data;

    private final String id;

    public UppercaseStringTask(final String id, final String data) {
        super(id);
        this.id = id;
        this.data = data;
    }

    @Override
    public String call() throws Exception {
        if (isFirstForConsumer()) {
            System.out.println("First time consumer sees id " + id);
        } else {
            System.out.println("Already seen id " + id);
        }
        if (data.equals("die")) {
            throw new Exception("data told me to throw");
        }
        return data.toUpperCase();
    }
}
