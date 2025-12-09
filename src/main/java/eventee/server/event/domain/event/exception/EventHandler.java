package eventee.server.event.domain.event.exception;

import com.server.eventee.global.exception.BaseException;
import com.server.eventee.global.exception.codes.BaseCode;

public class EventHandler extends BaseException{
  public EventHandler(BaseCode code) {
    super(code);
  }

}