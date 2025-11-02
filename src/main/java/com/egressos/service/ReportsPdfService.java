package com.egressos.service;

import com.egressos.dao.EventosDao;
import com.egressos.model.EventoChave;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ReportsPdfService {
    private final EventosDao eventosDao = new EventosDao();

    public java.nio.file.Path exportEventosBasicoPdf(java.nio.file.Path destino) throws Exception {
        List<EventoChave> eventos = eventosDao.listarTodos().stream()
                .sorted(Comparator.comparing(EventoChave::getData,
                        java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder())).reversed())
                .collect(Collectors.toList());

        Document doc = new Document(PageSize.A4, 36,36,36,36);
        PdfWriter.getInstance(doc, new FileOutputStream(destino.toFile()));
        doc.open();
        doc.add(new Paragraph("Relatório de Eventos", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
        doc.add(new Paragraph("Gerado em: " + java.time.LocalDateTime.now()));
        doc.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(5);
        table.setWidths(new int[]{16, 14, 26, 22, 22});
        table.addCell("Data");
        table.addCell("Tipo");
        table.addCell("Título");
        table.addCell("Organização");
        table.addCell("Local");

        for (EventoChave e : eventos){
            table.addCell(e.getData()==null? "" : e.getData().toString());
            table.addCell(e.getTipo()==null? "" : e.getTipo().name());
            table.addCell(n(e.getTitulo()));
            table.addCell(n(e.getOrganizacao()));
            table.addCell(n(e.getLocal()));
        }
        doc.add(table);
        doc.close();
        return destino;
    }

    private static String n(String s){ return s==null? "" : s; }
}
