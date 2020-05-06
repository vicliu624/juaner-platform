package indi.vicliu.juaner.ndbcluster.analyze.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.*;
import lombok.Data;

@Table(name = "processlist")
@Data
public class Processlist implements Serializable {
    @Column(name = "thd_id")
    private Long thdId;

    @Column(name = "conn_id")
    private Long connId;

    private String user;

    private String db;

    private String command;

    private String state;

    private Long time;

    private BigDecimal progress;

    @Column(name = "rows_examined")
    private Long rowsExamined;

    @Column(name = "rows_sent")
    private Long rowsSent;

    @Column(name = "rows_affected")
    private Long rowsAffected;

    @Column(name = "tmp_tables")
    private Long tmpTables;

    @Column(name = "tmp_disk_tables")
    private Long tmpDiskTables;

    @Column(name = "full_scan")
    private String fullScan;

    @Column(name = "last_wait")
    private String lastWait;

    private String source;

    @Column(name = "trx_state")
    private String trxState;

    @Column(name = "trx_autocommit")
    private String trxAutocommit;

    private String pid;

    @Column(name = "program_name")
    private String programName;

    @Column(name = "current_statement")
    private String currentStatement;

    @Column(name = "statement_latency")
    private String statementLatency;

    @Column(name = "lock_latency")
    private String lockLatency;

    @Column(name = "last_statement")
    private String lastStatement;

    @Column(name = "last_statement_latency")
    private String lastStatementLatency;

    @Column(name = "current_memory")
    private String currentMemory;

    @Column(name = "last_wait_latency")
    private String lastWaitLatency;

    @Column(name = "trx_latency")
    private String trxLatency;

    private static final long serialVersionUID = 1L;
}