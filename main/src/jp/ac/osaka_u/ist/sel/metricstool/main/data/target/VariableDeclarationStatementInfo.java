package jp.ac.osaka_u.ist.sel.metricstool.main.data.target;


import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;


/**
 * 変数宣言文の情報を保有するクラス
 * 
 * @author t-miyake
 *
 */
public class VariableDeclarationStatementInfo extends SingleStatementInfo implements ConditionInfo {

    /**
     * 宣言されている変数，初期化式，位置情報を与えて初期化
     * 宣言されている変数が初期化されている場合，このコンストラクタを使用する
     * 
     * @param variableDeclaration 宣言されているローカル変数
     * @param initializationExpression 初期化式
     * @param fromLine 開始行
     * @param fromColumn 開始列
     * @param toLine 終了行
     * @param toColumn 終了列
     */
    public VariableDeclarationStatementInfo(final LocalVariableUsageInfo variableDeclaration,
            final ExpressionInfo initializationExpression, final int fromLine,
            final int fromColumn, final int toLine, final int toColumn) {
        super(fromLine, fromColumn, toLine, toColumn);

        if (null == variableDeclaration) {
            throw new IllegalArgumentException("declaredVariable is null");
        }

        this.variableDeclaration = variableDeclaration;
        this.initializationExpression = initializationExpression;

    }

    @Override
    public int compareTo(ExcutableElement o) {
        int result = super.compareTo(o);

        if (0 == result && o instanceof VariableDeclarationStatementInfo) {
            LocalVariableInfo argVariable = ((VariableDeclarationStatementInfo) o)
                    .getDeclaredLocalVariable();

            return this.getDeclaredLocalVariable().compareTo(argVariable);
        } else {
            return result;
        }

    }

    /**
     * この宣言文で宣言されている変数を返す
     * 
     * @return この宣言文で宣言されている変数
     */
    public final LocalVariableInfo getDeclaredLocalVariable() {
        return this.variableDeclaration.getUsedVariable();
    }

    /**
     * 宣言されている変数の初期化式を返す
     * 
     * @return 宣言されている変数の初期化式．初期化されてい場合はnull
     */
    public final ExpressionInfo getInitializationExpression() {
        return this.initializationExpression;
    }

    /**
     * 宣言されている変数が初期化されているかどうかを返す
     * 
     * @return 宣言されている変数が初期化されていればtrue
     */
    public boolean isInitialized() {
        return null != this.initializationExpression;
    }

    @Override
    public Set<VariableUsageInfo<? extends VariableInfo<? extends UnitInfo>>> getVariableUsages() {
        final Set<VariableUsageInfo<? extends VariableInfo<? extends UnitInfo>>> usages = new TreeSet<VariableUsageInfo<? extends VariableInfo<? extends UnitInfo>>>();

        usages.add(this.variableDeclaration);
        if(this.isInitialized()) {
            usages.addAll(this.getInitializationExpression().getVariableUsages());
        }

        return Collections.unmodifiableSet(usages);
    }

    /**
     * 宣言されている変数を表すフィールド
     */
    private final LocalVariableUsageInfo variableDeclaration;

    /**
     * 宣言されている変数の初期化式を表すフィールド
     */
    private final ExpressionInfo initializationExpression;

}
