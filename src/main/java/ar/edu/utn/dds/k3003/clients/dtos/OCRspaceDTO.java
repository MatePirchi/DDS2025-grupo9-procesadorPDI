package ar.edu.utn.dds.k3003.clients.dtos;


import java.util.List;

public record OCRspaceDTO( List <parsedResult> parsedResults, int ocrExitCode, boolean isErroredOnProcessing,
                          String SearchablePDFURL, int ProcessingTimeInMilliseconds) {
    public record parsedResult(List <TextOverlay> txtOverlays, int fileParseExitCode, String parsedText,
                               String ErrorMessage, String ErrorDetails){}
    public record TextOverlay(List<Lines> lines, boolean hasOverlay, String message) {}
    public record Lines(List<Words> wordsInLines, int maxHeight, int minHeight){}
    public record Words (String wordText, int left, int top, int height, int width){}
}
