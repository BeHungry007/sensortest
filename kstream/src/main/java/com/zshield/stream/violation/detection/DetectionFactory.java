package com.zshield.stream.violation.detection;

import com.zshield.httpServer.domain.Violation;

public class DetectionFactory {
    public static Detection build(Violation violation) {
        return ((Violation.Conversion) violation.getRule()).conversionDetections(violation.getRule_id(), violation.getDescription(), violation.getRule_type());
    }
}
