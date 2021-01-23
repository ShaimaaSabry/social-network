package com.socialnetwork.util;

import java.io.IOException;

public interface PhotoVerificationService {
	int countFaceAnnotations(String gcsPath) throws IOException;
}
