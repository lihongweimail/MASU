package jp.ac.osaka_u.ist.sel.metricstool.main.data.target.unresolved;


import jp.ac.osaka_u.ist.sel.metricstool.main.security.MetricsToolSecurityManager;


/**
 * 未解決 do ブロックを表すクラス
 * 
 * @author y-higo
 */
public final class UnresolvedDoBlock extends UnresolvedBlock {

    /**
     * do ブロック情報を初期化
     */
    public UnresolvedDoBlock() {
        MetricsToolSecurityManager.getInstance().checkAccess();
    }
}
