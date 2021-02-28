package com.nexblocks.authguard.external.email.jmail;

import com.icegreen.greenmail.util.GreenMail;

import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;

public class GreenMailMessagesVisitor {
    private final GreenMail greenMail;
    private int offset;

    public GreenMailMessagesVisitor(final GreenMail greenMail) {
        this.greenMail = greenMail;
        this.offset = 0;
    }

    public List<MimeMessage> getUnread() {
        final List<MimeMessage> messages = new ArrayList<>();

        for (int i = offset; i < greenMail.getReceivedMessages().length; i++) {
            messages.add(greenMail.getReceivedMessages()[i]);
        }

        offset = greenMail.getReceivedMessages().length;

        return messages;
    }
}
