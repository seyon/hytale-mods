package dev.seyon.leveling.config;

/**
 * EXP curve configuration
 */
public class ExpCurveConfig {
    private String type = "exponential"; // linear, exponential, custom
    private double base = 100.0;
    private double multiplier = 1.5;
    private String custom_formula = "base * pow(multiplier, level - 1)";

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getBase() {
        return base;
    }

    public void setBase(double base) {
        this.base = base;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public String getCustomFormula() {
        return custom_formula;
    }

    public void setCustomFormula(String custom_formula) {
        this.custom_formula = custom_formula;
    }

    /** Merge from loaded: non-null/non-default from wins. */
    public void mergeFrom(ExpCurveConfig from) {
        if (from == null) return;
        if (from.type != null) this.type = from.type;
        this.base = from.base;
        this.multiplier = from.multiplier;
        if (from.custom_formula != null) this.custom_formula = from.custom_formula;
    }

    /**
     * Calculate EXP required for a given level
     */
    public double calculateExpForLevel(int level) {
        return switch (type.toLowerCase()) {
            case "linear" -> base * level;
            case "exponential" -> base * Math.pow(multiplier, level - 1);
            case "custom" -> evaluateCustomFormula(level);
            default -> base * Math.pow(multiplier, level - 1);
        };
    }

    /**
     * Evaluate custom formula
     * Simple implementation - supports basic math expressions
     */
    private double evaluateCustomFormula(int level) {
        try {
            // Replace variables
            String formula = custom_formula
                .replace("base", String.valueOf(base))
                .replace("multiplier", String.valueOf(multiplier))
                .replace("level", String.valueOf(level));
            
            // Use Java's built-in math for pow function
            if (formula.contains("pow(")) {
                // Simple pow extraction: pow(a, b)
                int powStart = formula.indexOf("pow(");
                int powEnd = formula.indexOf(")", powStart);
                if (powStart >= 0 && powEnd > powStart) {
                    String powContent = formula.substring(powStart + 4, powEnd);
                    String[] parts = powContent.split(",");
                    if (parts.length == 2) {
                        double a = Double.parseDouble(parts[0].trim());
                        double b = Double.parseDouble(parts[1].trim());
                        double result = Math.pow(a, b);
                        formula = formula.substring(0, powStart) + result + formula.substring(powEnd + 1);
                    }
                }
            }
            
            // Evaluate simple arithmetic
            return evaluateSimpleExpression(formula);
        } catch (Exception e) {
            // Fallback to exponential
            return base * Math.pow(multiplier, level - 1);
        }
    }

    /**
     * Evaluate simple arithmetic expression
     */
    private double evaluateSimpleExpression(String expr) {
        expr = expr.trim();
        
        // Handle multiplication
        if (expr.contains("*")) {
            String[] parts = expr.split("\\*");
            double result = Double.parseDouble(parts[0].trim());
            for (int i = 1; i < parts.length; i++) {
                result *= Double.parseDouble(parts[i].trim());
            }
            return result;
        }
        
        // Handle addition
        if (expr.contains("+")) {
            String[] parts = expr.split("\\+");
            double result = 0;
            for (String part : parts) {
                result += Double.parseDouble(part.trim());
            }
            return result;
        }
        
        // Just a number
        return Double.parseDouble(expr);
    }
}
