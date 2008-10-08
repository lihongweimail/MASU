package jp.ac.osaka_u.ist.sel.metricstool.main.ast.databuilder.expression;


import jp.ac.osaka_u.ist.sel.metricstool.main.ast.databuilder.BuildDataManager;
import jp.ac.osaka_u.ist.sel.metricstool.main.ast.token.AstToken;
import jp.ac.osaka_u.ist.sel.metricstool.main.ast.token.BuiltinTypeToken;
import jp.ac.osaka_u.ist.sel.metricstool.main.ast.token.ConstantToken;
import jp.ac.osaka_u.ist.sel.metricstool.main.ast.visitor.AstVisitEvent;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.unresolved.UnresolvedArrayTypeInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.unresolved.UnresolvedClassTypeInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.unresolved.UnresolvedLiteralUsageInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.unresolved.UnresolvedReferenceTypeInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.unresolved.UnresolvedTypeInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.unresolved.UnresolvedTypeParameterInfo;


/**
 * @author kou-tngt
 *
 */
public class TypeElementBuilder extends ExpressionBuilder {

    /**
     * @param expressionManager
     */
    public TypeElementBuilder(final ExpressionElementManager expressionManager,
            final BuildDataManager buildDataManager) {
        super(expressionManager, buildDataManager);
    }

    @Override
    protected void afterExited(final AstVisitEvent event) {
        final AstToken token = event.getToken();
        if (token.isTypeDescription()) {
            this.buildType();
        } else if (token.isArrayDeclarator()) {
            this.buildArrayType();
        } else if (token.isTypeArgument()) {
            buildTypeArgument();
        } else if (token.isTypeWildcard()) {
            buildTypeWildCard();
        } else if (token instanceof BuiltinTypeToken) {
            this.buildBuiltinType((BuiltinTypeToken) token);
        } else if (token instanceof ConstantToken) {
            this.buildConstantElement((ConstantToken) token, event.getStartLine(), event
                    .getStartColumn(), event.getEndLine(), event.getEndColumn());
        }
    }

    protected void buildArrayType() {
        final ExpressionElement[] elements = this.getAvailableElements();

        assert (elements.length > 0) : "Illegal state: type description was not found.";

        TypeElement typeElement = null;
        if (elements.length > 0) {
            if (elements[0] instanceof IdentifierElement) {
                final UnresolvedTypeInfo referenceType = this.buildReferenceType(elements);
                typeElement = TypeElement.getInstance(UnresolvedArrayTypeInfo.getType(
                        referenceType, 1));
            } else if (elements[0] instanceof TypeElement) {
                typeElement = ((TypeElement) elements[0]).getArrayDimensionInclementedInstance();
            }
        }

        if (null != typeElement) {
            this.pushElement(typeElement);
        }
    }

    protected void buildType() {
        final ExpressionElement[] elements = this.getAvailableElements();

        assert (elements.length > 0) : "Illegal state: type description was not found.";

        if (elements.length > 0) {
            if (elements[0] instanceof IdentifierElement) {
                this.pushElement(TypeElement.getInstance(this.buildReferenceType(elements)));
            } else if (elements[0] instanceof TypeElement) {
                assert (elements.length == 1) : "Illegal state: unexpected type arguments.";
                this.pushElement(elements[0]);
            }
        }
    }

    /**
     * 型引数を表す式要素を構築する．
     */
    protected void buildTypeArgument() {
        //利用できる全要素を取得
        final ExpressionElement[] elements = this.getAvailableElements();

        assert (elements.length > 0) : "Illegal state: type arguments were not created.";

        assert (elements.length == 1) : "Illegal state: too many type arguments.";

        if (elements.length > 0) {
            ExpressionElement element = elements[elements.length - 1];

            assert (element instanceof TypeElement) : "Illegal state: unspecified type argument.";

            if (element instanceof TypeElement) {
                //一番最後が型要素だったら型引数要素を作成
                TypeArgumentElement argument = new TypeArgumentElement(((TypeElement) element)
                        .getType());
                //それ以外の要素を全部もとに戻す．
                int size = elements.length - 1;
                for (int i = 0; i < size; i++) {
                    pushElement(elements[i]);
                }
                //最後に型引数要素を登録する
                pushElement(argument);
            }
        }
    }

    protected void buildTypeWildCard() {
        UnresolvedTypeInfo upperBounds = getTypeUpperBounds();

        assert (null != upperBounds);

        pushElement(TypeElement.getInstance(upperBounds));
    }

    protected UnresolvedTypeInfo getTypeUpperBounds() {
        final ExpressionElement[] elements = this.getAvailableElements();

        UnresolvedTypeInfo resultType = null;

        if (elements.length > 0) {

            assert (elements.length == 1) : "Illegal state: too many type upper bounds.";

            ExpressionElement element = elements[elements.length - 1];

            assert (element instanceof TypeElement) : "Illegal state: upper bounds type was not type element.";

            if (element instanceof TypeElement) {
                resultType = ((TypeElement) element).getType();
            }
        }

        //一応元に戻してみる
        int size = elements.length - 1;
        for (int i = 0; i < size; i++) {
            pushElement(elements[i]);
        }

        return resultType;
    }

    protected UnresolvedTypeInfo buildReferenceType(final ExpressionElement[] elements) {
        assert (elements.length > 0);
        assert (elements[0] instanceof IdentifierElement);

        IdentifierElement element = (IdentifierElement) elements[0];
        final String[] typeName = element.getQualifiedName();

        UnresolvedTypeParameterInfo typeParameter = null;
        if (typeName.length == 1) {
            typeParameter = this.buildDataManager.getTypeParameter(typeName[0]);
        }

        if (null != typeParameter) {
            return typeParameter;
        }

        //TODO 型パラメータに型引数が付く言語があったらそれを登録する仕組みを作る必要があるかも

        UnresolvedClassTypeInfo resultType = new UnresolvedClassTypeInfo(this.buildDataManager
                .getAllAvaliableNames(), typeName);

        for (int i = 1; i < elements.length; i++) {
            assert (elements[i] instanceof TypeArgumentElement) : "Illegal state: type argument was unexpected type";
            TypeArgumentElement typeArugument = (TypeArgumentElement) elements[i];

            // TODO C#などは参照型以でも型引数を指定できるので、その対処が必要かも           
            assert typeArugument.getType() instanceof UnresolvedReferenceTypeInfo : "Illegal state: type argument was not reference type.";
            resultType.addTypeArgument((UnresolvedReferenceTypeInfo) typeArugument.getType());
        }

        return resultType;
    }

    protected void buildBuiltinType(final BuiltinTypeToken token) {
        this.pushElement(TypeElement.getInstance(token.getType()));
    }

    protected void buildConstantElement(final ConstantToken token, final int fromLine,
            final int fromColumn, final int toLine, final int toColumn) {
        final UnresolvedLiteralUsageInfo literal = new UnresolvedLiteralUsageInfo(token.toString(),
                token.getType());
        literal.setFromLine(fromLine);
        literal.setFromColumn(fromColumn);
        literal.setToLine(toLine);
        literal.setToColumn(toColumn);

        this.pushElement(new TypeElement(literal));
    }

    @Override
    protected boolean isTriggerToken(final AstToken token) {
        return token.isBuiltinType() || token.isTypeDescription() || token.isConstant()
                || token.isArrayDeclarator() || token.isTypeArgument() || token.isTypeWildcard();
    }

}
