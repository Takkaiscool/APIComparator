public class Test {
    @org.testng.annotations.Test
      public  void test1(){
        Comparator comp=new Comparator("file1.txt","file2.txt");
        comp.compareResponses();
    }
}
