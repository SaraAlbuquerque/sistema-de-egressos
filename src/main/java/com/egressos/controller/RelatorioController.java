package com.egressos.controller;

import com.egressos.service.ReportsPdfService;
import com.egressos.service.ReportsService;

import java.nio.file.Path;
import java.time.LocalDate;

public class RelatorioController {

    public enum TipoRelatorio {
        EVENTOS_POR_TIPO,
        EVENTOS_POR_ORGANIZACAO,
        EVENTOS_POR_PERIODO,
        EVENTOS_POR_LOCAL,
        EVENTOS_BASICO
    }

    private final ReportsService reportsService;
    private final ReportsPdfService reportsPdfService;

    public RelatorioController() {
        this.reportsService = new ReportsService();
        this.reportsPdfService = new ReportsPdfService();
    }

    public Path gerarCsv(TipoRelatorio tipo, Path destino, LocalDate inicio, LocalDate fim, String filtroLocal) throws Exception {
        switch (tipo) {
            case EVENTOS_POR_TIPO:
                return reportsService.exportEventosPorTipoCsv(destino);
            case EVENTOS_POR_ORGANIZACAO:
                return reportsService.exportEventosPorOrganizacaoCsv(destino);
            case EVENTOS_POR_PERIODO:
                return reportsService.exportEventosPorPeriodoCsv(destino, inicio, fim);
            case EVENTOS_POR_LOCAL:
                return reportsService.exportEventosPorLocalCsv(destino, filtroLocal);
            default:
                throw new IllegalArgumentException("Tipo de relatório inválido para CSV");
        }
    }

    public Path gerarPdf(TipoRelatorio tipo, Path destino) throws Exception {
        if (tipo == TipoRelatorio.EVENTOS_BASICO) {
            reportsPdfService.exportEventosBasicoPdf(destino);
            return destino;
        }
        throw new IllegalArgumentException("Tipo de relatório inválido para PDF");
    }
}
