package net.lunglet.sre2008;

import net.lunglet.sre2008.v2.Answer;

public final class Trial extends Segment {
    private final Answer answer;

    public Trial(final String name, final int channel, final Answer answer) {
        super(name, channel);
        this.answer = answer;
    }

    public Trial(final String name, final String channel, final String answer) {
        super(name, channel);
        if (answer != null && answer.toLowerCase().startsWith("targ")) {
            this.answer = Answer.TARGET;
        } else if (answer != null && answer.toLowerCase().startsWith("non")) {
            this.answer = Answer.NONTARGET;
        } else if (answer != null || answer.toLowerCase().startsWith("bad")) {
            this.answer = Answer.BAD;
        } else if (answer == null || answer.toLowerCase().startsWith("unk")) {
            this.answer = Answer.UNKNOWN;
        } else {
            throw new IllegalArgumentException("invalid answer");
        }
    }

    public Answer getAnswer() {
        return answer;
    }

    public String getAnswerString() {
        return answer.toString().toLowerCase();
    }
    
    public boolean isTarget() {
        return Answer.TARGET.equals(answer);
    }

    @Override
    public String toString() {
        return super.toString() + " (" + getAnswerString() + ")";
    }
}
