package sample;

import com.amazonaws.services.cloudtrail.processinglibrary.exceptions.CallbackException;
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.RecordFilter;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailClientRecord;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailRecord;

public class SampleRecordFilter implements RecordFilter{

    private static final String EC2_EVENTS = "ec2.amazonaws.com";

    /**
     * Record filter that only keep EC2 deletion API calls.
     */
    @Override
    public boolean filterRecord(CloudTrailClientRecord clientRecord) throws CallbackException {
        CloudTrailRecord record = clientRecord.getRecord();

        String eventSource = record.getEventSource();
        String eventName = record.getEventName();

        return eventSource.equals(EC2_EVENTS) && eventName.startsWith("Delete");
    }
}
