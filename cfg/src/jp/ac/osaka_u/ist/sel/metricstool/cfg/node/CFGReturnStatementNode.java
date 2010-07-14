package jp.ac.osaka_u.ist.sel.metricstool.cfg.node;

import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.ReturnStatementInfo;

/**
 * return文を表すノード
 * 
 * @author higo
 * 
 */
public class CFGReturnStatementNode extends CFGStatementNode {

	/**
	 * 生成するノードに対応するreturn文を与えて初期化
	 * 
	 * @param returnStatement
	 */
	CFGReturnStatementNode(final ReturnStatementInfo returnStatement) {
		super(returnStatement);
	}
}
