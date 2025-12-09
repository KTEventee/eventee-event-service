package eventee.server.event.domain.event.exception;


import eventee.server.event.global.exception.BaseException;
import eventee.server.event.global.exception.codes.BaseCode;

public class EventHandler extends BaseException {
  public EventHandler(BaseCode code) {
    super(code);
  }

}