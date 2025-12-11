package eventee.server.event.domain.event.exception;


import eventee.server.common.exception.BaseException;
import eventee.server.common.exception.codes.BaseCode;

public class EventHandler extends BaseException {
  public EventHandler(BaseCode code) {
    super(code);
  }

}