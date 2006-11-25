package jp.ac.osaka_u.ist.sel.metricstool.main.data.accessor;


import jp.ac.osaka_u.ist.sel.metricstool.main.data.metric.MetricAlreadyRegisteredException;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.MethodInfo;


/**
 * このインターフェースは，メソッドメトリクスを登録するためのメソッド群を提供する．
 * 
 * @author y-higo
 *
 */
public interface MethodMetricsRegister {

    /**
     * 第一引数のメソッドのメトリクス値（第二引数）を登録する
     * 
     * @param methodInfo メトリクスの計測対象メソッド
     * @param value メトリクス値
     * @throws 登録しようとしているメトリクスが既に登録されている場合にスローされる
     */
    void registMetric(MethodInfo methodInfo, int value) throws MetricAlreadyRegisteredException;
}
