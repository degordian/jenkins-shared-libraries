package com.bornfight

@Grab(group='com.google.api-client', module='google-api-client', version='1.30.1')
@Grab(group='com.google.apis', module='google-api-services-sheets', version='v4-rev581-1.25.0')

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

class DeployedTagsTracker implements Serializable{

    private static final String CREDENTIALS_FILE_PATH = "credentials.json";

    private static def getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream is = SheetsQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (is == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        return GoogleCredential.fromStream(is).createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
    }

    static def update(String sheetId, String project, String stage, String tag){
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final String range = "DeploymentTracker!A2:C";
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        ValueRange currentState = service.spreadsheets().values().get(sheetId, range).execute();
        System.out.println("Project Staging Production");
        for(List<Object> row: currentState.getValues()){
            for(Object cell: row){
                System.out.print(cell+" ");
            }
            System.out.println();
        }
        List<List<Object>> values = Arrays.asList(
                Arrays.asList(
                        "Test1234", "Test123"
                )
        );
        ValueRange body = new ValueRange()
                .setValues(values);
        UpdateValuesResponse result =
                service.spreadsheets().values().update(sheetId, range, body)
                        .setValueInputOption("RAW")
                        .execute();
        System.out.printf("%d cells updated.", result.getUpdatedCells());
        ValueRange response = service.spreadsheets().values()
                .get(sheetId, range)
                .execute();

    }

}