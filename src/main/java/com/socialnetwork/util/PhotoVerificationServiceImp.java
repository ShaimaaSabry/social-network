package com.socialnetwork.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageSource;

@Service
public class PhotoVerificationServiceImp  implements PhotoVerificationService {
	
	public int countFaceAnnotations(String gcsPath) throws IOException {
		ImageSource imgSource = ImageSource.newBuilder().setGcsImageUri(gcsPath).build();
		Image img = Image.newBuilder().setSource(imgSource).build();

		Feature feat = Feature.newBuilder().setType(Feature.Type.FACE_DETECTION).build();

		List<AnnotateImageRequest> requests = new ArrayList<>();
		AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
		requests.add(request);

		try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
			BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
			List<AnnotateImageResponse> responses = response.getResponsesList();

			int faceAnnotationsCount = 0;
			for (AnnotateImageResponse res : responses) {
				faceAnnotationsCount += res.getFaceAnnotationsCount();
			}
			return faceAnnotationsCount;
		}
	}
}
