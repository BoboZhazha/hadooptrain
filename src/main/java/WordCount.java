import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class WordCount {


    // Mapper<LongWritable, Text, Text, LongWritable>泛型的4个参数指的是偏移量的类型,每一行的内容, k2的类型,v2的类型
    public static class MyMapper extends Mapper<LongWritable, Text, Text, LongWritable> {
        @Override
        protected void map(LongWritable k1, Text v1, Context context) throws IOException, InterruptedException {

            String[] splits = v1.toString().split(" ");

            for (String s : splits) {
                Text k2 = new Text(s);
                LongWritable v2 = new LongWritable(1L);
                context.write(k2, v2);
            }

        }
    }

    public static class MyReducer extends Reducer<Text, LongWritable, Text, LongWritable> {
        @Override
        protected void reduce(Text k2, Iterable<LongWritable> v2s, Context context) throws IOException, InterruptedException {

            long sum = 0L;
            for (LongWritable l : v2s) {
                sum += l.get();
            }
            context.write(k2, new LongWritable(sum));
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        //创建配置文件
        Configuration conf = new Configuration();
        //获取一个作业
        Job job = Job.getInstance(conf);

        //设置整个job所用的那些类在哪个jar包
        job.setJarByClass(WordCount.class);

        //本job使用的mapper和reducer的类
        job.setMapperClass(MyMapper.class);
        job.setReducerClass(MyReducer.class);

        //指定reduce的输出数据key-value类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);


        //指定mapper的输出数据key-value类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);

        //指定要处理的输入数据存放路径
        FileInputFormat.setInputPaths(job, new Path("hdfs://localhost:8020/wll.txt"));

        //指定处理结果的输出数据存放路径
        FileOutputFormat.setOutputPath(job, new Path("file:////Users/zhang/wc/output"));

        //将job提交给集群运行
        job.waitForCompletion(true);

    }
}
