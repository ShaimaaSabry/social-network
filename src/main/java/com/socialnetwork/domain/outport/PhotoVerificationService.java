package com.socialnetwork.domain.outport;

import java.io.IOException;

public interface PhotoVerificationService {
    int countFaces(String path) throws IOException;
}
