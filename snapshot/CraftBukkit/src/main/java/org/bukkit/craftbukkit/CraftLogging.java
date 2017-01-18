package org.bukkit.craftbukkit;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.server.CrashReport;
import net.minecraft.server.CrashReportSystemDetails;
import org.bukkit.Bukkit;

public class CraftLogging {

    public static void crashReport(CrashReport report) {
        crashReport(report, Bukkit.getLogger());
    }

    public static void crashReport(CrashReport report, Logger logger) {
        final StringBuilder text = new StringBuilder();
        for(CrashReportSystemDetails extra : report.extras()) {
            extra.a(text);
        }
        report.details().a(text);
        logger.log(Level.SEVERE, "Exception while " + report.activity() + "\n\n" + text, report.exception());
    }
}
