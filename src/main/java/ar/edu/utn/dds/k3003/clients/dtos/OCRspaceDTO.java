package ar.edu.utn.dds.k3003.clients.dtos;


import java.util.List;

public record OCRspaceDTO(List <parsedResult> ParsedResults, int OCRExitCode, boolean IsErroredOnProcessing,
                          String SearchablePDFURL, int ProcessingTimeInMilliseconds) {

    public record parsedResult(TextOverlay TextOverlay, String TextOrientation, int FileParseExitCode, String ParsedText,
                               String ErrorMessage, String ErrorDetails){}
    public record TextOverlay(List<Lines> Lines, boolean HasOverlay, String Message) {}
    public record Lines(List<Words> Words, int MaxHeight, int MinHeight){}
    public record Words (String WordText, int Left, int Top, int Height, int Width){}
}
