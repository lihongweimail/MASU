package jp.ac.osaka_u.ist.sel.metricstool.main.data.target.unresolved;


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;

import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.CallableUnitInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.ClassInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.ClassInfoManager;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.ClassTypeInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.FieldInfoManager;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.MethodInfoManager;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.TargetClassInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.TargetInnerClassInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.TypeInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.UnknownTypeInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.external.ExternalClassInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.security.MetricsToolSecurityManager;


/**
 * 未解決参照型を表すクラス
 * 
 * @author y-higo
 * 
 */
public class UnresolvedClassTypeInfo implements UnresolvedReferenceTypeInfo {

    /**
     * 利用可能な名前空間名，参照名を与えて初期化
     * 
     * @param availableNamespaces 名前空間名
     * @param referenceName 参照名
     */
    public UnresolvedClassTypeInfo(final AvailableNamespaceInfoSet availableNamespaces,
            final String[] referenceName) {

        // 不正な呼び出しでないかをチェック
        MetricsToolSecurityManager.getInstance().checkAccess();
        if ((null == availableNamespaces) || (null == referenceName)) {
            throw new NullPointerException();
        }

        this.availableNamespaceSet = availableNamespaces;
        this.referenceName = referenceName;
        //this.fullReferenceName = referenceName;
        //this.ownerType = null;
        this.typeParameterUsages = new LinkedList<UnresolvedClassTypeInfo>();
    }

    @Override
    public boolean alreadyResolved() {
        return null != this.resolvedInfo;
    }

    @Override
    public TypeInfo getResolvedType() {

        if (!this.alreadyResolved()) {
            throw new NotResolvedException();
        }

        return this.resolvedInfo;
    }

    @Override
    public TypeInfo resolveType(TargetClassInfo usingClass, CallableUnitInfo usingMethod,
            ClassInfoManager classInfoManager, FieldInfoManager fieldInfoManager,
            MethodInfoManager methodInfoManager) {

        // 不正な呼び出しでないかをチェック
        MetricsToolSecurityManager.getInstance().checkAccess();
        if (null == classInfoManager) {
            throw new NullPointerException();
        }

        // 既に解決済みである場合は，キャッシュを返す
        if (this.alreadyResolved()) {
            return this.getResolvedType();
        }

        //　単項参照の場合
        if (this.isMoniminalReference()) {

            //　インポートされているパッケージ内のクラスから検索
            for (final AvailableNamespaceInfo availableNamespace : this.getAvailableNamespaces()) {

                // import aaa.bbb.*の場合 (クラス名の部分が*)
                if (availableNamespace.isAllClasses()) {

                    //　利用可能なクラス一覧を取得し，そこから検索
                    final String[] namespace = availableNamespace.getNamespace();
                    for (final ClassInfo availableClass : classInfoManager.getClassInfos(namespace)) {

                        //　参照されているクラスが見つかった
                        if (this.referenceName[0].equals(availableClass.getClassName())) {
                            // TODO 型パラメータの情報を保存する処理が必要
                            this.resolvedInfo = new ClassTypeInfo(availableClass);
                            return this.resolvedInfo;
                        }
                    }

                    // import aaa.bbb.CCCの場合　(クラス名まで記述されている)
                } else {

                    ClassInfo referencedClass = classInfoManager.getClassInfo(availableNamespace
                            .getImportName());
                    // null の場合は外部クラスの参照とみなす
                    if (null == referencedClass) {
                        referencedClass = new ExternalClassInfo(availableNamespace.getImportName());
                        classInfoManager.add((ExternalClassInfo) referencedClass);
                    }

                    // TODO　型パラメータの情報を格納する処理が必要
                    this.resolvedInfo = new ClassTypeInfo(referencedClass);
                    return this.resolvedInfo;
                }
            }

            // デフォルトパッケージからクラスを検索
            for (final ClassInfo availableClass : classInfoManager.getClassInfos(new String[0])) {

                // 参照されているクラスが見つかった
                if (this.referenceName[0].equals(availableClass.getClassName())) {
                    // TODO　型パラメータの情報を保存する処理が必要
                    this.resolvedInfo = new ClassTypeInfo(availableClass);
                    return this.resolvedInfo;
                }
            }

            // 複数項参照の場合
        } else {

            //　インポートされているクラスの子クラスから検索
            AVAILABLENAMESPACE: for (final AvailableNamespaceInfo availableNamespace : this
                    .getAvailableNamespaces()) {

                // import aaa.bbb.*の場合 (クラス名の部分が*)
                if (availableNamespace.isAllClasses()) {

                    // 利用可能なクラス一覧を取得し，そこから検索
                    final String[] namespace = availableNamespace.getNamespace();
                    for (final ClassInfo availableClass : classInfoManager.getClassInfos(namespace)) {

                        //　参照されているクラスが見つかった
                        if (this.referenceName[0].equals(availableClass.getClassName())) {

                            // 対象クラスでない場合は内部クラス情報はわからないのでスキップ
                            if (!(availableClass instanceof TargetClassInfo)) {
                                continue AVAILABLENAMESPACE;
                            }

                            // 対象クラスの場合は，順に内部クラスをたどって行く
                            TargetClassInfo currentClass = (TargetClassInfo) availableClass;
                            INDEX: for (int index = 1; index < this.referenceName.length; index++) {
                                final SortedSet<TargetInnerClassInfo> innerClasses = currentClass
                                        .getInnerClasses();
                                for (final TargetInnerClassInfo innerClass : innerClasses) {

                                    if (this.referenceName[index].equals(innerClass.getClassName())) {
                                        currentClass = innerClass;
                                        continue INDEX;
                                    }

                                    // ここに到達するのは，クラスが見つからなかった場合
                                    this.resolvedInfo = UnknownTypeInfo.getInstance();
                                    return this.resolvedInfo;
                                }
                            }

                            //　ここに到達するのは，クラスが見つかった場合
                            this.resolvedInfo = new ClassTypeInfo(currentClass);
                            // TODO 型パラメータの処理が必要
                            return this.resolvedInfo;
                        }
                    }

                    // import aaa.bbb.CCCの場合 (クラス名まで記述されている)
                } else {

                    ClassInfo importClass = classInfoManager.getClassInfo(availableNamespace
                            .getImportName());

                    //　null の場合はその(外部)クラスを表すオブジェクトを作成 
                    if (null == importClass) {
                        importClass = new ExternalClassInfo(availableNamespace.getImportName());
                        classInfoManager.add((ExternalClassInfo) importClass);
                    }

                    // importClassが対象クラスでない場合は内部クラス情報がわからないのでスキップ
                    if (!(importClass instanceof TargetClassInfo)) {
                        continue AVAILABLENAMESPACE;
                    }

                    // 対象クラスの場合は，順に内部クラスをたどって行く
                    TargetClassInfo currentClass = (TargetClassInfo) importClass;
                    INDEX: for (int index = 1; index < this.referenceName.length; index++) {
                        final SortedSet<TargetInnerClassInfo> innerClasses = currentClass
                                .getInnerClasses();
                        for (final TargetInnerClassInfo innerClass : innerClasses) {

                            if (this.referenceName[index].equals(innerClass.getClassName())) {
                                currentClass = innerClass;
                                continue INDEX;
                            }

                            // ここに到達するのは，クラスが見つからなかった場合
                            this.resolvedInfo = UnknownTypeInfo.getInstance();
                            return this.resolvedInfo;
                        }
                    }

                    //　ここに到達するのは，クラスが見つかった場合
                    this.resolvedInfo = new ClassTypeInfo(currentClass);
                    // TODO 型パラメータの処理が必要
                    return this.resolvedInfo;
                }
            }
        }

        this.resolvedInfo = UnknownTypeInfo.getInstance();
        return this.resolvedInfo;
    }

    /**
     * 利用可能な名前空間，型の完全修飾名を与えて初期化
     * @param referenceName 型の完全修飾名
     */
    public UnresolvedClassTypeInfo(final String[] referenceName) {
        this(new AvailableNamespaceInfoSet(), referenceName);
    }

    ///**
    // * 利用可能な名前空間名，参照名を与えて初期化
    // * 
    // * @param availableNamespaces 名前空間名
    // * @param referenceName 参照名
    // */
    /*
    public UnresolvedReferenceTypeInfo(final AvailableNamespaceInfoSet availableNamespaces,
            final String[] referenceName, final UnresolvedReferenceTypeInfo ownerType) {

        // 不正な呼び出しでないかをチェック
        MetricsToolSecurityManager.getInstance().checkAccess();
        if ((null == availableNamespaces) || (null == referenceName) || (null == ownerType)) {
            throw new NullPointerException();
        }

        this.availableNamespaceSet = availableNamespaces;
        String[] ownerReferenceName = ownerType.getFullReferenceName();
        String[] fullReferenceName = new String[referenceName.length+ownerReferenceName.length];
        System.arraycopy(ownerReferenceName, 0, fullReferenceName, 0, ownerReferenceName.length);
        System.arraycopy(referenceName, 0, fullReferenceName, ownerReferenceName.length, referenceName.length);
        this.fullReferenceName = fullReferenceName;
        this.referenceName = referenceName;
        this.ownerType = ownerType;
        this.typeParameterUsages = new LinkedList<UnresolvedReferenceTypeInfo>();
    }
    */
    /**
     * 型パラメータ使用を追加する
     * 
     * @param typeParameterUsage 追加する型パラメータ使用
     */
    public final void addTypeArgument(final UnresolvedClassTypeInfo typeParameterUsage) {

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
    public final List<UnresolvedClassTypeInfo> getTypeArguments() {
        return Collections.unmodifiableList(this.typeParameterUsages);
    }

    /**
     * この参照型の名前を返す
     * 
     * @return この参照型の名前を返す
     */
    public final String getTypeName() {
        return this.referenceName[this.referenceName.length - 1];
    }

    ///**
    // * この参照型のownerも含めた参照名を返す
    // * 
    // * @return この参照型のownerも含めた参照名を返す
    // */
    /*public final String[] getFullReferenceName() {
        return this.fullReferenceName;
    }*/

    /**
     * この参照型の参照名を返す
     * 
     * @return この参照型の参照名を返す
     */
    public final String[] getReferenceName() {
        return this.referenceName;
    }

    ///**
    // * この参照型がくっついている未解決参照型を返す
    // * 
    // * @return この参照型がくっついている未解決参照型
    // */
    /*public final UnresolvedReferenceTypeInfo getOwnerType() {
        return this.ownerType;
    }*/

    ///**
    // * この参照型が，他の参照型にくっついているかどうかを返す
    // * 
    // * @return くっついている場合は true，くっついていない場合は false
    // */
    /*public final boolean hasOwnerReference() {
        return null != this.ownerType;
    }*/

    /**
     * この参照型の参照名を引数で与えられた文字で結合して返す
     * 
     * @param delimiter 結合に用いる文字
     * @return この参照型の参照名を引数で与えられた文字で結合した文字列
     */
    public final String getReferenceName(final String delimiter) {

        if (null == delimiter) {
            throw new NullPointerException();
        }

        final StringBuilder sb = new StringBuilder(this.referenceName[0]);
        for (int i = 1; i < this.referenceName.length; i++) {
            sb.append(delimiter);
            sb.append(this.referenceName[i]);
        }

        return sb.toString();
    }

    /**
     * この参照型の完全限定名として可能性のある名前空間名の一覧を返す
     * 
     * @return この参照型の完全限定名として可能性のある名前空間名の一覧
     */
    public final AvailableNamespaceInfoSet getAvailableNamespaces() {
        return this.availableNamespaceSet;
    }

    /**
     * この参照が単項かどうかを返す
     * 
     * @return　単項である場合はtrue，そうでない場合はfalse
     */
    public final boolean isMoniminalReference() {
        return 1 == this.referenceName.length;
    }

    public final static UnresolvedClassTypeInfo getInstance(UnresolvedClassInfo referencedClass) {
        return new UnresolvedClassTypeInfo(referencedClass.getFullQualifiedName());
    }
    
    public final UnresolvedClassReferenceInfo getUsage() {
        UnresolvedClassReferenceInfo usage = new UnresolvedClassReferenceInfo(this.availableNamespaceSet, referenceName);
        for (UnresolvedReferenceTypeInfo typeArgument : this.typeParameterUsages) {
            usage.addTypeArgument(typeArgument);
        }
        return usage;
    }

    /**
     * 利用可能な名前空間名を保存するための変数，名前解決処理の際に用いる
     */
    private final AvailableNamespaceInfoSet availableNamespaceSet;

    /**
     * 参照名を保存する変数
     */
    private final String[] referenceName;

    ///**
    // * ownerも含めた参照名を保存する変数
    // */
    //private final String[] fullReferenceName;

    ///**
    // * この参照がくっついている未解決参照型を保存する変数
    // */
    //private final UnresolvedReferenceTypeInfo ownerType;

    /**
     * 型引数参照を保存するための変数
     */
    private final List<UnresolvedClassTypeInfo> typeParameterUsages;

    private TypeInfo resolvedInfo;

}
