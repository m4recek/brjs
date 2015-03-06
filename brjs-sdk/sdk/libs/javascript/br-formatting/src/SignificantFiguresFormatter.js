/**
 * @module br/formatting/SignificantFiguresFormatter
 */

/**
 * @class
 * @alias module:br/formatting/SignificantFiguresFormatter
 * @implements module:br/formatting/Formatter
 *
 * @classdesc
 * Formats a number to the specified number of significant figures.
 *
 * <p><code>SignificantFiguresFormatter</code> is typically used with Presenter, but can be invoked programmatically
 * as in the following example which evaluates to "3.142":</p>
 *
 * <pre>br.formatting.SignificantFiguresFormatter.format(3.14159, {sf:4})</pre>
 */
br.formatting.SignificantFiguresFormatter = function() {
};

br.Core.implement(br.formatting.SignificantFiguresFormatter, br.formatting.Formatter);

/**
 * Formats a number to the specified number of significant figures.
 *
 * <p>
 * Attribute Options:
 * </p>
 * <p>
 * <table>
 * <tr>
 * <th>Option</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>sf</td><td>  the number of significant figures to apply (does nothing if omitted).</td></tr>
 * </table>
 *
 * @param {Variant} vValue  the number (String or Number type).
 * @param {Map} mAttributes  the map of attributes.
 * @return  the number, formatted to the specified precision.
 * @type  String
 */
br.formatting.SignificantFiguresFormatter.prototype.format = function(vValue, mAttributes) {
	return br.util.Number.isNumber(vValue) ? String(br.util.Number.toPrecision(vValue, mAttributes["sf"])) : vValue;
};

/**
 * @private
 */
br.formatting.SignificantFiguresFormatter.prototype.toString = function() {
	return "br.formatting.SignificantFiguresFormatter";
};