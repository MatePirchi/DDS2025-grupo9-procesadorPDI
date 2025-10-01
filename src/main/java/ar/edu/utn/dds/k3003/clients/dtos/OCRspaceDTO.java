package ar.edu.utn.dds.k3003.clients.dtos;


import java.util.List;

public record OCRspaceDTO(List<ParsedResults> parsedResults) {

    private class ParsedResults {
        int fileParseExitCode;
        String parsedText;
        String ErrorMessage;
        String ErrorDetails;
    }
    private class TextOverlay {
        List<Lines> lines;
        boolean hasOverlay;
        String message;
    }
    private class Lines{
        List<Words> wordsInLines;
        int maxHeight;
        int minHeight;
    }
    private class Words{
        String wordText;
        int left;
        int top;
        int height;
        int width;
    }
}
