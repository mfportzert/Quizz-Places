package com.quizz.places.application;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.annotation.ReportsCrashes;

import com.quizz.core.application.BaseQuizzApplication;

@ReportsCrashes(formKey = "",
				mailTo = "tagsphere.droid@gmail.com",
				customReportContent = 
					{
						ReportField.APP_VERSION_CODE,
						ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL,
						ReportField.CUSTOM_DATA, ReportField.STACK_TRACE
					},
				formUri = "https://tagspheredroid.cloudant.com/acra-worldtourquiz/_design/acra-storage/_update/report")
public class QuizzPlacesApplication extends BaseQuizzApplication {

	public static final String IMAGES_DIR = "images/";
	public static final String JSON_FILE = "places.json";
	
	public void onCreate()
	{
		super.onCreate();
		ACRA.init(this);
	}
}
