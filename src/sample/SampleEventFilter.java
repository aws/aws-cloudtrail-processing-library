package sample;

import com.amazonaws.services.cloudtrail.processinglibrary.exceptions.CallbackException;
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.EventFilter;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailEvent;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailEventData;

public class SampleEventFilter implements EventFilter{

    private static final String EC2_EVENTS = "ec2.amazonaws.com";

    /**
     * Event filter that only keep EC2 deletion API calls.
     */
    @Override
    public boolean filterEvent(CloudTrailEvent event) throws CallbackException {
        CloudTrailEventData eventData = event.getEventData();

        String eventSource = eventData.getEventSource();
        String eventName = eventData.getEventName();

        return eventSource.equals(EC2_EVENTS) && eventName.startsWith("Delete");
    }
}
