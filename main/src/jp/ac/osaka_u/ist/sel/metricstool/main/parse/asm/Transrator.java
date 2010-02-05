package jp.ac.osaka_u.ist.sel.metricstool.main.parse.asm;


import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import jp.ac.osaka_u.ist.sel.metricstool.main.data.DataManager;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.ArrayTypeInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.ClassInfoManager;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.ClassTypeInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.ExternalClassInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.PrimitiveTypeInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.SuperTypeParameterInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.TypeInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.TypeParameterInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.TypeParameterizable;


public class Transrator {

    public static String[] transrateName(final String unresolvedName) {

        if (null == unresolvedName) {
            throw new IllegalArgumentException();
        }

        final List<String> name = new LinkedList<String>();
        final StringTokenizer tokenizer = new StringTokenizer(unresolvedName, "/$");
        while (tokenizer.hasMoreElements()) {
            name.add(tokenizer.nextToken());
        }
        return name.toArray(new String[0]);
    }

    /**
     * 未解決型情報を名前解決するクラス.
     * 第二，第三引数は，TypeParameterを解決する場合のみ指定すればよい.
     * しかし，解決する型が内部にジェネリクスを含んでいる場合があるので，
     * 第三引数はきちんと指定することが重要
     * 
     * @param unresolvedType
     * @param index
     * @param ownerUnit
     * @return
     */
    public static TypeInfo translateType(final String unresolvedType, final int index,
            final TypeParameterizable ownerUnit) {

        if (null == unresolvedType) {
            throw new IllegalArgumentException();
        }

        // 一文字ならば，primitiveTypeでなければならない
        if (1 == unresolvedType.length()) {
            return translatePrimitiveType(unresolvedType.charAt(0));
        }

        // '['で始まっているときは配列
        else if ('[' == unresolvedType.charAt(0)) {
            final TypeInfo subType = translateType(unresolvedType.substring(1), index, ownerUnit);

            // もともと配列ならば事件を1つ増やす
            if (subType instanceof ArrayTypeInfo) {
                final ArrayTypeInfo subArrayType = (ArrayTypeInfo) subType;
                final TypeInfo ElementType = subArrayType.getElementType();
                final int dimension = subArrayType.getDimension();
                return ArrayTypeInfo.getType(ElementType, dimension + 1);
            }

            //　配列でないなら配列にする
            else {
                return ArrayTypeInfo.getType(subType, 1);
            }
        }

        // 配列でない参照型の場合
        else if (('L' == unresolvedType.charAt(0))
                && (';' == unresolvedType.charAt(unresolvedType.length() - 1))) {

            final ClassInfoManager classInfoManager = DataManager.getInstance()
                    .getClassInfoManager();
            final String unresolvedReferenceType = unresolvedType.substring(1, unresolvedType
                    .length() - 1);

            // ジェネリクスがない場合
            if ((-1 == unresolvedReferenceType.indexOf('<'))
                    && (-1 == unresolvedReferenceType.lastIndexOf('>'))) {

                final String[] referenceTypeName = transrateName(unresolvedReferenceType);
                ExternalClassInfo referenceClass = (ExternalClassInfo) classInfoManager
                        .getClassInfo(referenceTypeName);
                if (null == referenceClass) {
                    referenceClass = new ExternalClassInfo(referenceTypeName);
                    classInfoManager.add(referenceClass);
                }
                return new ClassTypeInfo(referenceClass);

            }

            //　ジェネリクスがある場合
            else if ((0 <= unresolvedReferenceType.indexOf('<'))
                    && (0 <= unresolvedReferenceType.lastIndexOf('>'))) {

                final String[] referenceTypeName = transrateName(unresolvedReferenceType.substring(
                        0, unresolvedReferenceType.indexOf('<')));

                // クラス名の部分を解決
                ExternalClassInfo referenceClass = (ExternalClassInfo) classInfoManager
                        .getClassInfo(referenceTypeName);
                if (null == referenceClass) {
                    referenceClass = new ExternalClassInfo(referenceTypeName);
                    classInfoManager.add(referenceClass);
                }
                final ClassTypeInfo referenceType = new ClassTypeInfo(referenceClass);

                //ジェネリクス部分を解決し，順次クラス参照にその情報を追加                
                final String[] typeArguments = getTypeArguments(unresolvedReferenceType);
                for (int i = 0; i < typeArguments.length; i++) {
                    final TypeInfo typeParameter = translateType(typeArguments[i], i, ownerUnit);
                    referenceType.addTypeArgument(typeParameter);
                }

                return referenceType;

            } else {
                throw new IllegalStateException();
            }
        }

        // ジェネリクス(L...;)の場合
        else if ('T' == unresolvedType.charAt(0)
                && ';' == unresolvedType.charAt(unresolvedType.length() - 1)) {

            final String identifier = unresolvedType.substring(1, unresolvedType.length() - 1);
            return new TypeParameterInfo(ownerUnit, identifier, index, null);
        }

        // ジェネリクス(+...;)の場合
        else if ('+' == unresolvedType.charAt(0)
                && ';' == unresolvedType.charAt(unresolvedType.length() - 1)) {

            final String unresolvedExtendsName = unresolvedType.substring(1, unresolvedType
                    .length() - 1);
            final String[] extendsName = transrateName(unresolvedExtendsName);
            final ClassInfoManager classInfoManager = DataManager.getInstance()
                    .getClassInfoManager();
            ExternalClassInfo extendsClass = (ExternalClassInfo) classInfoManager
                    .getClassInfo(extendsName);
            if (null == extendsClass) {
                extendsClass = new ExternalClassInfo(extendsName);
                classInfoManager.add(extendsClass);
            }
            return new TypeParameterInfo(ownerUnit, "?", index, new ClassTypeInfo(extendsClass));
        }

        // ジェネリクス(-...;)の場合
        else if ('-' == unresolvedType.charAt(0)
                && ';' == unresolvedType.charAt(unresolvedType.length() - 1)) {

            final String unresolvedSuperName = unresolvedType.substring(1,
                    unresolvedType.length() - 1);
            final String[] superName = transrateName(unresolvedSuperName);
            final ClassInfoManager classInfoManager = DataManager.getInstance()
                    .getClassInfoManager();
            ExternalClassInfo superClass = (ExternalClassInfo) classInfoManager
                    .getClassInfo(superName);
            if (null == superClass) {
                superClass = new ExternalClassInfo(superName);
                classInfoManager.add(superClass);
            }
            return new SuperTypeParameterInfo(ownerUnit, "?", index, null, new ClassTypeInfo(
                    superClass));
        }

        throw new IllegalArgumentException();
    }

    private static TypeInfo translatePrimitiveType(final char c) {

        switch (c) {
        case 'Z':
            return PrimitiveTypeInfo.BOOLEAN;
        case 'C':
            return PrimitiveTypeInfo.CHAR;
        case 'B':
            return PrimitiveTypeInfo.BYTE;
        case 'S':
            return PrimitiveTypeInfo.SHORT;
        case 'I':
            return PrimitiveTypeInfo.INT;
        case 'F':
            return PrimitiveTypeInfo.FLOAT;
        case 'J':
            return PrimitiveTypeInfo.LONG;
        case 'D':
            return PrimitiveTypeInfo.DOUBLE;
        default:
            throw new IllegalArgumentException();
        }
    }

    private static String[] getTypeArguments(final String type) {

        final List<String> typeArguments = new LinkedList<String>();

        int fromIndex = 0;
        int toIndex = 0;
        int nestLevel = 0;
        for (int index = 0; index < type.length(); index++) {

            // '<'を見つけた時の処理
            if ('<' == type.charAt(index)) {
                nestLevel += 1;
                if (1 == nestLevel) {
                    fromIndex = index + 1;
                }
            }

            // '>'を見つけた時の処理
            else if ('>' == type.charAt(index)) {
                nestLevel -= 1;
            }

            // ';'を見つけた時の処理
            else if (';' == type.charAt(index)) {

                if (1 == nestLevel) {
                    toIndex = index + 1;
                    typeArguments.add(type.substring(fromIndex, toIndex));
                    fromIndex = index + 1;
                }
            }

            // '*'　を見つけた時の処理
            else if ('*' == type.charAt(index)) {

                if (1 == nestLevel) {
                    typeArguments.add("*");
                    fromIndex = index + 1;
                    toIndex = index + 1;
                }
            }
        }

        if (0 != nestLevel) {
            throw new IllegalStateException();
        }

        return typeArguments.toArray(new String[0]);
    }
}
