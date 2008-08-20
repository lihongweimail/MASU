package jp.ac.osaka_u.ist.sel.metricstool.main.data.target.unresolved;


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.CallInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.CallableUnitInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.ClassInfoManager;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.ClassReferenceInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.ClassTypeInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.EntityUsageInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.FieldInfoManager;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.MethodInfoManager;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.ReferenceTypeInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.TargetClassInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.TypeInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.UnknownEntityUsageInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.external.ExternalClassInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.io.DefaultMessagePrinter;
import jp.ac.osaka_u.ist.sel.metricstool.main.io.MessagePrinter;
import jp.ac.osaka_u.ist.sel.metricstool.main.io.MessageSource;
import jp.ac.osaka_u.ist.sel.metricstool.main.io.MessagePrinter.MESSAGE_TYPE;
import jp.ac.osaka_u.ist.sel.metricstool.main.security.MetricsToolSecurityManager;


/**
 * 未解決メンバ(メソッド，コンストラクタ)呼び出しを保存するためのクラス
 * 
 * @author t-miyake, higo
 * @param <T> 解決済みの型
 */
public abstract class UnresolvedCallInfo<T extends CallInfo> extends UnresolvedEntityUsageInfo<T> {

    /**
     * オブジェクトを初期化
     */
    public UnresolvedCallInfo() {

        MetricsToolSecurityManager.getInstance().checkAccess();

        this.typeArguments = new LinkedList<UnresolvedReferenceTypeInfo>();
        this.parameterTypes = new LinkedList<UnresolvedEntityUsageInfo<?>>();

    }

    /**
     * 型パラメータ使用を追加する
     * 
     * @param typeParameterUsage 追加する型パラメータ使用
     */
    public final void addTypeArgument(final UnresolvedReferenceTypeInfo typeParameterUsage) {

        MetricsToolSecurityManager.getInstance().checkAccess();
        if (null == typeParameterUsage) {
            throw new NullPointerException();
        }

        this.typeArguments.add(typeParameterUsage);
    }

    /**
     * 引数を追加
     * 
     * @param typeInfo
     */
    public final void addParameter(final UnresolvedEntityUsageInfo<?> typeInfo) {

        // 不正な呼び出しでないかをチェック
        MetricsToolSecurityManager.getInstance().checkAccess();
        if (null == typeInfo) {
            throw new NullPointerException();
        }

        this.parameterTypes.add(typeInfo);
    }

    /**
     * 引数の List を返す
     * 
     * @return 引数の List
     */
    public final List<UnresolvedEntityUsageInfo<?>> getParameters() {
        return Collections.unmodifiableList(this.parameterTypes);
    }

    /**
     * 型パラメータ使用の List を返す
     * 
     * @return 型パラメータ使用の List
     */
    public final List<UnresolvedReferenceTypeInfo> getTypeArguments() {
        return Collections.unmodifiableList(this.typeArguments);
    }

    /**
     * 
     * @param unresolvedParameters
     * @return
     */
    protected final List<EntityUsageInfo> resolveParameters(final TargetClassInfo usingClass,
            final CallableUnitInfo usingMethod, final ClassInfoManager classInfoManager,
            final FieldInfoManager fieldInfoManager, final MethodInfoManager methodInfoManager) {

        //　解決済み実引数を格納するための変数
        final List<EntityUsageInfo> parameters = new LinkedList<EntityUsageInfo>();

        for (final UnresolvedEntityUsageInfo<?> unresolvedParameter : this.getParameters()) {

            EntityUsageInfo parameter = unresolvedParameter.resolve(usingClass, usingMethod,
                    classInfoManager, fieldInfoManager, methodInfoManager);

            assert parameter != null : "resolveEntityUsage returned null!";

            if (parameter instanceof UnknownEntityUsageInfo) {

                // クラス参照だった場合
                if (unresolvedParameter instanceof UnresolvedClassReferenceInfo) {

                    final ExternalClassInfo externalClassInfo = NameResolver
                            .createExternalClassInfo((UnresolvedClassReferenceInfo) unresolvedParameter);
                    classInfoManager.add(externalClassInfo);
                    final ClassTypeInfo referenceType = new ClassTypeInfo(externalClassInfo);
                    for (final UnresolvedTypeInfo unresolvedTypeArgument : ((UnresolvedClassReferenceInfo) unresolvedParameter)
                            .getTypeArguments()) {
                        final TypeInfo typeArgument = unresolvedTypeArgument.resolve(usingClass,
                                usingMethod, classInfoManager, fieldInfoManager, methodInfoManager);
                        referenceType.addTypeArgument(typeArgument);
                    }

                    // 使用位置を取得
                    final int fromLine = this.getFromLine();
                    final int fromColumn = this.getFromColumn();
                    final int toLine = this.getToLine();
                    final int toColumn = this.getToColumn();

                    parameter = new ClassReferenceInfo(referenceType, fromLine, fromColumn, toLine,
                            toColumn);

                } else {
                    assert false : "Here shouldn't be reached!";
                }
            }
            parameters.add(parameter);
        }

        return parameters;
    }

    protected final List<ReferenceTypeInfo> resolveTypeArguments(final TargetClassInfo usingClass,
            final CallableUnitInfo usingMethod, final ClassInfoManager classInfoManager,
            final FieldInfoManager fieldInfoManager, final MethodInfoManager methodInfoManager) {

        //　解決済み型引数を格納するための変数
        final List<ReferenceTypeInfo> typeArguments = new LinkedList<ReferenceTypeInfo>();

        for (final UnresolvedReferenceTypeInfo unresolvedTypeArgument : this.getTypeArguments()) {

            TypeInfo typeArgument = unresolvedTypeArgument.resolve(usingClass, usingMethod,
                    classInfoManager, fieldInfoManager, methodInfoManager);

            assert typeArgument != null : "resolveEntityUsage returned null!";

            typeArguments.add((ReferenceTypeInfo) typeArgument);
        }

        return typeArguments;
    }

    /**
     * 型パラメータ使用を保存するための変数
     */
    protected List<UnresolvedReferenceTypeInfo> typeArguments;

    /**
     * 引数を保存するための変数
     */
    protected List<UnresolvedEntityUsageInfo<?>> parameterTypes;

    /**
     * エラーメッセージ出力用のプリンタ
     */
    protected static final MessagePrinter err = new DefaultMessagePrinter(new MessageSource() {
        public String getMessageSourceName() {
            return "UnresolvedMethodCall";
        }
    }, MESSAGE_TYPE.ERROR);
}
