package jp.ac.osaka_u.ist.sel.metricstool.main.data.target;


import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 * 外部クラスに定義されているメソッド情報を保存するためのクラス
 * 
 * @author higo
 */
@SuppressWarnings("serial")
public final class ExternalMethodInfo extends MethodInfo {

    /**
     * 外部クラスに定義されているメソッドオブジェクトを初期化する
     * 
     * @param methodName メソッド名
     * @param ownerClass このメソッドを定義しているクラス
     */
    public ExternalMethodInfo(final String methodName, final ExternalClassInfo ownerClass) {

        super(new HashSet<ModifierInfo>(), methodName, ownerClass, false, true, true, true, 0, 0,
                0, 0);

        this.setReturnType(UnknownTypeInfo.getInstance());
    }

    /**
     * 外部クラスに定義されているメソッドオブジェクトを初期化する．
     * 定義しているクラスが不明な場合に用いるコンストラクタ
     * 
     * @param methodName メソッド名
     */
    public ExternalMethodInfo(final String methodName) {

        super(new HashSet<ModifierInfo>(), methodName, ExternalClassInfo.UNKNOWN, false, true,
                true, true, 0, 0, 0, 0);
        this.setReturnType(UnknownTypeInfo.getInstance());
    }

    /**
     * ExternalMethodInfoでは中身はない
     */
    @Override
    public SortedSet<StatementInfo> getStatements() {
        return Collections.unmodifiableSortedSet(new TreeSet<StatementInfo>());
    }

    /**
     * ExternalMethodInfo では利用できない
     */
    @Override
    public final boolean isInheritanceVisible() {
        throw new CannotUseException();
    }

    /**
     * ExternalMethodInfo では利用できない
     */
    @Override
    public final boolean isNamespaceVisible() {
        throw new CannotUseException();
    }

    /**
     * ExternalMethodInfo では利用できない
     */
    @Override
    public final boolean isPrivateVisible() {
        throw new CannotUseException();
    }

    /**
     * ExternalMethodInfo では利用できない
     */
    @Override
    public final boolean isPublicVisible() {
        throw new CannotUseException();
    }

    /**
     * ExternalMethodInfo では利用できない
     */
    @Override
    public Set<ModifierInfo> getModifiers() {
        throw new CannotUseException();
    }

    /**
     * ExternalClassInfo では利用できない
     */
    @Override
    public TypeParameterInfo getTypeParameter(int index) {
        throw new CannotUseException();
    }

    /**
     * ExternalClassInfo では利用できない
     */
    @Override
    public List<TypeParameterInfo> getTypeParameters() {
        throw new CannotUseException();
    }

    /**
     * ExternalClassInfo では利用できない
     */
    @Override
    public void addTypeParameter(TypeParameterInfo typeParameter) {
        throw new CannotUseException();
    }

    /**
     * ExternalClassInfo では利用できない
     */
    @Override
    public void addTypeParameterUsage(TypeParameterInfo typeParameterInfo, TypeInfo usedType) {
        throw new CannotUseException();
    }

    /**
     * ExternalClassInfo では利用できない
     */
    @Override
    public Map<TypeParameterInfo, TypeInfo> getTypeParameterUsages() {
        throw new CannotUseException();
    }
}
