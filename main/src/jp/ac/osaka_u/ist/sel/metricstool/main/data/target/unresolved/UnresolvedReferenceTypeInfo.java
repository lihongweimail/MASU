package jp.ac.osaka_u.ist.sel.metricstool.main.data.target.unresolved;


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import jp.ac.osaka_u.ist.sel.metricstool.main.security.MetricsToolSecurityManager;


/**
 * 未解決参照型を表すクラス
 * 
 * @author y-higo
 * 
 */
public final class UnresolvedReferenceTypeInfo implements UnresolvedTypeInfo {

    /**
     * 名前空間名，参照名を与えて初期化
     * 
     * @param namespace 名前空間名
     * @param referenceName 参照名
     */
    public UnresolvedReferenceTypeInfo(final AvailableNamespaceInfoSet availableNamespaceSet,
            final String[] referenceName) {

        // 不正な呼び出しでないかをチェック
        MetricsToolSecurityManager.getInstance().checkAccess();
        if ((null == availableNamespaceSet) || (null == referenceName)) {
            throw new NullPointerException();
        }

        this.availableNamespaceSet = availableNamespaceSet;
        this.referenceName = referenceName;
        this.typeParameterUsages = new LinkedList<UnresolvedTypeParameterUsage>();
    }

    /**
     * 型パラメータ使用を追加する
     * 
     * @param typeParameterUsage 追加する型パラメータ使用
     */
    public void addTypeParameterUsage(final UnresolvedTypeParameterUsage typeParameterUsage) {

        // 不正な呼び出しでないかをチェック
        MetricsToolSecurityManager.getInstance().checkAccess();
        if (null == typeParameterUsage) {
            throw new NullPointerException();
        }
        
        this.typeParameterUsages.add(typeParameterUsage);
    }

    /**
     * このクラス参照で使用されている型パラメータの List を返す
     * 
     * @return このクラス参照で使用されている型パラメータの List
     */
    public List<UnresolvedTypeParameterUsage> getTypeParameterUsages(){
        return Collections.unmodifiableList(this.typeParameterUsages);
    }
    
    /**
     * この参照型の名前を返す
     * 
     * @return この参照型の名前を返す
     */
    public String getTypeName() {
        final String[] referenceName = this.getReferenceName();
        return referenceName[referenceName.length - 1];
    }

    /**
     * この参照型の参照名を返す
     * 
     * @return この参照型の参照名を返す
     */
    public String[] getReferenceName() {
        return this.referenceName;
    }

    /**
     * この参照型の完全限定名として可能性のある名前空間名の一覧を返す
     * 
     * @return この参照型の完全限定名として可能性のある名前空間名の一覧
     */
    public AvailableNamespaceInfoSet getAvailableNamespaces() {
        return this.availableNamespaceSet;
    }

    /**
     * 利用可能な名前空間名を保存するための変数，名前解決処理の際に用いる
     */
    private final AvailableNamespaceInfoSet availableNamespaceSet;

    /**
     * 参照名を保存する変数
     */
    private final String[] referenceName;

    /**
     * 未解決型パラメータ使用を保存するための変数
     */
    private final List<UnresolvedTypeParameterUsage> typeParameterUsages;
}
