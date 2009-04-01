package jp.ac.osaka_u.ist.sel.metricstool.cfg;


import java.util.Set;

import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.SingleStatementInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.UnitInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.VariableInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.VariableUsageInfo;


/**
 * CFG�̕��m�[�h��\���N���X
 * @author t-miyake
 *
 */
public class CFGStatementNode extends CFGNormalNode<SingleStatementInfo> {

    /**
     * ��������m�[�h�ɑΉ����镶��^���ď�����
     * @param statement ��������m�[�h�ɑΉ����镶
     */
    public CFGStatementNode(final SingleStatementInfo statement) {
        super(statement);
    }
}