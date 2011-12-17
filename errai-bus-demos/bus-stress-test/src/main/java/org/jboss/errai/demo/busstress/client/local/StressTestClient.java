package org.jboss.errai.demo.busstress.client.local;

import org.jboss.errai.bus.client.api.Message;
import org.jboss.errai.bus.client.api.MessageCallback;
import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.bus.client.api.builder.MessageBuildSendable;
import org.jboss.errai.bus.client.framework.MessageBus;
import org.jboss.errai.bus.client.protocols.MessageParts;
import org.jboss.errai.ioc.client.api.EntryPoint;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

@EntryPoint
public class StressTestClient extends Composite {

  private static StressTestClientUiBinder uiBinder = GWT.create(StressTestClientUiBinder.class);
  @UiField Label messageSendCount;
  @UiField Label messageSendBytes;
  @UiField Label messageRecvCount;
  @UiField Label messageRecvBytes;

  @UiField IntegerBox messageInterval;
  @UiField Label messageIntervalError;

  @UiField IntegerBox messageSize;
  @UiField Label messageSizeError;

  @UiField Label inFlightCount;

  @UiField Button startButton;
  @UiHandler("startButton")
  public void onStartButtonClick(ClickEvent click) {
    restart();
  }

  @UiField Button stopButton;
  @UiHandler("stopButton")
  void onStopButtonClick(ClickEvent event) {
    stopIfRunning();
  }


  @Inject private MessageBus bus;

  private Timer sendTimer;

  private Stats stats = new Stats();

  static class Stats {
    int inflightMessages;
    int totalWaitTime;

    int receivedBytes;
    int receivedMessages;

    int sentBytes;
    int sentMessages;

    public void registerReceivedMessage(Message message) {
      inflightMessages--;
      receivedMessages++;
      receivedBytes += message.get(String.class, MessageParts.Value).length();
    }

    public void registerSentMessage(Message message) {
      inflightMessages++;
      sentMessages++;
      sentBytes += message.get(String.class, MessageParts.Value).length();
    }
  }

  /**
   * The message payload that gets sent to the server.
   */
  private String messageValue;

  interface StressTestClientUiBinder extends UiBinder<Widget, StressTestClient> {
  }

  public StressTestClient() {
    initWidget(uiBinder.createAndBindUi(this));

    RootPanel.get().add(this);
  }

  public void restart() {
    if (!validateSettings()) {
      return;
    }
    stopIfRunning();

    // create the message payload
    Integer messageSizeInt = messageSize.getValue();
    StringBuilder sb = new StringBuilder(messageSizeInt);
    for (int i = 0; i < messageSizeInt; i++) {
      sb.append("!");
    }
    messageValue = sb.toString();

    sendTimer = new Timer() {
      @Override public void run() {
        MessageBuildSendable sendable = MessageBuilder.createMessage()
        .toSubject("StressTestService")
        .withValue(messageValue)
        .done()
        .repliesTo(new MessageCallback() {
          @Override
          public void callback(Message message) {
            stats.registerReceivedMessage(message);
            updateStatsLabels();
          }
        });
        sendable.sendNowWith(bus);
        stats.registerSentMessage(sendable.getMessage());
        updateStatsLabels();
      }
    };
    sendTimer.scheduleRepeating(messageInterval.getValue());
  }

  private boolean validateSettings() {
    boolean valid = true;

    if (messageSize.getValue() == null) {
      valid = false;
      messageSizeError.setText("Numbers only");
      messageSize.addStyleName("error");
    }
    else {
      messageSizeError.setText("");
      messageSize.removeStyleName("error");
    }

    if (messageInterval.getValue() == null) {
      valid = false;
      messageIntervalError.setText("Numbers only");
      messageInterval.addStyleName("error");
    }
    else {
      messageIntervalError.setText("");
      messageInterval.removeStyleName("error");
    }

    return valid;
  }

  /**
   * Stops the timer if it's running. Does nothing otherwise. Safe to call any time.
   */
  void stopIfRunning() {
    if (sendTimer != null) {
      sendTimer.cancel();
      sendTimer = null;
    }
  }

  /**
   * Updates the labels in the UI based on the values in {@link #stats}.
   */
  private void updateStatsLabels() {
    inFlightCount.setText("" + stats.inflightMessages);
    messageSendBytes.setText("" + stats.sentBytes);
    messageSendCount.setText("" + stats.sentMessages);
    messageRecvBytes.setText("" + stats.receivedBytes);
    messageRecvCount.setText("" + stats.receivedMessages);
  }

}
