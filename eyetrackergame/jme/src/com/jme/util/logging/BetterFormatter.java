package com.jme.util.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class BetterFormatter extends Formatter {

    Date dat = new Date();
//    private final static String format = "{0,date} {0,time}";
    private final static String format = "{0,time}";
    private MessageFormat formatter;

    private Object args[] = new Object[1];

    // Line separator string. This is the value of the line.separator
    // property at the moment that the SimpleFormatter was created.
    private String lineSeparator = (String) java.security.AccessController
            .doPrivileged(new sun.security.action.GetPropertyAction(
                    "line.separator"));

    /**
     * Format the given LogRecord.
     * 
     * @param record
     *            the log record to be formatted.
     * @return a formatted log record
     */
    public synchronized String format(LogRecord record) {
        StringBuffer sb = new StringBuffer();
        
        dat.setTime(record.getMillis());
        args[0] = dat;
        StringBuffer text = new StringBuffer();
        if (formatter == null) {
            formatter = new MessageFormat(format);
        }
        formatter.format(args, text, null);
        sb.append(text);

        sb.append(" [");
        sb.append(record.getThreadID());
        sb.append("] ");

        if (record.getSourceClassName() != null) {
            sb.append(record.getSourceClassName());
        } else {
            sb.append(record.getLoggerName());
        }
        if (record.getSourceMethodName() != null) {
            sb.append("->");
            sb.append(record.getSourceMethodName());
            sb.append("()");
        }

        sb.append(" - ");
        String message = formatMessage(record);
        sb.append(record.getLevel().getLocalizedName());
        sb.append(": ");
        sb.append(message);
        sb.append(" ");

        Object[] params = record.getParameters();
        if (params != null) {
            sb.append("{");
            for (int i=0; i<params.length; i++) {
                sb.append(params[i]);
                if (i < params.length - 1) {
                    sb.append(", ");
                }
            }            
            sb.append("}");
//            sb.append(lineSeparator);
        }
        
        // Find all the threads
//        ThreadGroup root = Thread.currentThread().getThreadGroup().getParent();
//        while (root.getParent() != null) {
//            root = root.getParent();
//        }
//        visit(sb, root, 0);

        if (record.getThrown() != null) {
            sb.append(lineSeparator);
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
                sb.append(sw.toString());
            } catch (Exception ex) {
            }
        }

        sb.append(lineSeparator);

        return sb.toString();
    }
    
    // This method recursively visits all thread groups under `group'.
    private void visit(StringBuffer sb, ThreadGroup group, int level) {
        // Get threads in `group'
        int numThreads = group.activeCount();
        Thread[] threads = new Thread[numThreads*2];
        numThreads = group.enumerate(threads, false);
    
        // Enumerate each thread in `group'
        for (int i=0; i<numThreads; i++) {
            // Get thread
            Thread thread = threads[i];
            
            sb.append(thread.getName() + "[" + thread.getId() + "]");
            sb.append(lineSeparator);
        }
    
        // Get thread subgroups of `group'
        int numGroups = group.activeGroupCount();
        ThreadGroup[] groups = new ThreadGroup[numGroups*2];
        numGroups = group.enumerate(groups, false);
    
        // Recursively visit each subgroup
        for (int i=0; i<numGroups; i++) {
            visit(sb, groups[i], level+1);
        }
    }
}
