import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

public class VertexCluster {

    //hashmap for storing the graph having vertices as its key and value as weight of edge
    private HashMap<ArrayList<String>,Integer> graph= new HashMap();

    //hashmap for cluster having key as particular vertex and value as integer array with index 0 as weight and 1 as id of cluster
    private HashMap<String,int[]> cluster= new HashMap();

    //duplicating the graph for forming a cluster and keeping track of remaining nodes
    private HashMap<ArrayList<String>,Integer> duplicateGraph=new HashMap<>();


    /*o	Two strings will be passed as vertex name and positive integer will be passed as weight of edge from main method.T
    he vertices will be sorted and stored as a key of hash map and weight will be stored as value of that vertices.
    To store vertices arraylist is used.
    For sorting two vertex checkvalue method is called which will return the greater string.
    If the set of vertices already exists, then it would not be entered and will return false.
    */
    public boolean addEdge(String vertex1,String vertex2,int weight)
    {
        //check the vertices are null or empty
        if(vertex1==null ||vertex2==null||vertex1.isEmpty() ||vertex2.isEmpty() ||vertex1.trim().equals("") || vertex2.trim().equals(""))
        {
            return false;
        }
        //checking the weight is valid positive integer
        if(weight<=0)
        {
            return false;
        }
        //checking the vertex1 and vertex2 are same or not
        if(vertex1.equalsIgnoreCase(vertex2))
        {
            return false;
        }

        ArrayList<String> list= new ArrayList<>();
        //checkValue will return the string having greater value in terms of ascii
        String check=checkValue(vertex1.trim(),vertex2.trim());
        if(check==null)
        {
            return false;
        }
        //setting up the vertices in sorted manner in arraylist
        else if(check.equalsIgnoreCase(vertex1.trim()))
        {
            list.add(vertex1.trim());
            list.add(vertex2.trim());
        }
        else if(check.equalsIgnoreCase(vertex2.trim()))
        {
            list.add(vertex2.trim());
            list.add(vertex1.trim());
        }
        //checking if vertices already exist
        if(graph.containsKey(list))
        {
            return false;
        }
        if(!checkExist(list))
        {
            return false;
        }
        //adding the vertex in graph
        graph.put(list,weight);
        return true;
    }


    /*Tolerance will be passed from the main method
    If the tolerance exceeds the result of formula it will not create a cluster.
    This method will call setCluster method to make the calculations and make the cluster
    After the clusters are set clusterVertices method will make sets of cluster and will return it to main method.
    if any problem is encountered it will return null
    */

    public Set<Set<String>> clusterVertices( float tolerance )
    {
        //checking if the graph is empty
        if(graph.isEmpty())
        {
            return null;
        }
        Set<Set<String>> finalSet= new HashSet<>();
        //setCluster will do all the computation for making cluster
        setCluster(tolerance);

        //converting hashmap cluster to set for final return
        for(String x:cluster.keySet())
        {
            Set<String> temp= new HashSet<>();
            int index=cluster.get(x)[1];

            for(String y:cluster.keySet())
            {
                if(cluster.get(y)[1]==index)
                {
                    temp.add(y);
                }
            }
            finalSet.add(temp);
        }
        return finalSet;
    }

    /*In this method duplicate graph will be initialized and also initialize cluster will be called.
    after that minimum weighted node will be found by using findMin method.
    Loop will traverse through the graph and will make calculation using formula.
    If the ratio will be less than the tolerance cluster will be created
    and all the vertices of merging clusters will be updated with weight and also cluster id will be changed.
    If vertices will be already in same cluster it will be ignored.
    */
    private void setCluster(float tolerance)
    {
        //duplicating the graph
        duplicateGraph.putAll(graph);
        //calling the method to initialize the cluster
        initializeCluster();
        float ratio;
        //looping till graph gets empty
        while (!duplicateGraph.isEmpty())
        {
            //finding minimum weight from graph
            int min=findMin();
            for (ArrayList x : graph.keySet())
            {
                //0 index weight and 1 index id
                if (min == graph.get(x))
                {
                    int[] vertex1=cluster.get(x.get(0).toString());
                    int[] vertex2=cluster.get(x.get(1).toString());

                    //if the vertices are in same cluster
                    if(vertex1[1]==vertex2[1])
                    {
                        duplicateGraph.remove(x);
                        continue;
                    }

                    //if vertices are not in same cluster
                    else if(vertex1[1]!=vertex2[1])
                    {
                        int edge=graph.get(x);

                        //calculating the ratio using the given formula
                        ratio=(float) edge/Math.min(vertex1[0],vertex2[0]);

                        if(ratio<=tolerance)
                        {
                            vertex1[0]=edge;
                            vertex2[0]=edge;
                            int id1=vertex1[1];
                            int id2=vertex2[1];
                            String change=null;
                            if(vertex1[1]>vertex2[1])
                            {
                                for(String y:cluster.keySet())
                                {
                                    if(cluster.get(y)[1]==vertex1[1] && !x.get(0).equals(y) && !x.get(1).equals(y))
                                    {
                                        change=y;
                                        break;
                                    }
                                }
                                vertex1[1]=vertex2[1];
                                if(change!=null)
                                {
                                    cluster.put(change,vertex1);
                                }

                            }
                            else
                            {
                                for(String y:cluster.keySet())
                                {
                                    if(cluster.get(y)[1]==vertex2[1] && !x.get(0).equals(y) && !x.get(1).equals(y))
                                    {
                                        change=y;
                                        break;
                                    }
                                }
                                vertex2[1]=vertex1[1];
                                if(change!=null)
                                {
                                    cluster.put(change,vertex2);
                                }

                            }

                            //updating the weight and cluster id
                            cluster.put(x.get(0).toString(),vertex1);
                            cluster.put(x.get(1).toString(),vertex2);

                            //updating value of all the vertices in cluster
                            for(String a:cluster.keySet())
                            {
                                if(cluster.get(a)[1]==id1 || cluster.get(a)[1]==id2)
                                {
                                    int weight=vertex1[0];
                                    int id=vertex1[1];
                                    cluster.put(a,new int[]{weight,id});
                                }
                            }
                        }
                        duplicateGraph.remove(x);
                    }
                }
            }
        }
    }

    /*This method is used by addEdges to check whether the pair of vertices exist or not
    This method will check all the possibilities accordingly and if the vertices exists it will return false*/

    private boolean checkExist(List<String> list)
    {
        //making 4 lists and checking different possibilities for existing vertices in graph
        List<String> list1=new ArrayList<>();
        List<String> list2=new ArrayList<>();
        List<String> list3=new ArrayList<>();
        list3.add(list.get(0).toUpperCase());
        list3.add(list.get(1));
        List<String> list4=new ArrayList<>();
        list4.add(list.get(0));
        list4.add(list.get(1).toUpperCase());
        for (String x:list)
        {
                list1.add(x.toUpperCase());
                list2.add(x.toLowerCase());
        }
        if(graph.containsKey(list1) || graph.containsKey(list2) || graph.containsKey(list3)|| graph.containsKey(list4))
        {
            return false;
        }
        else {
            return true;
        }
    }

    /*
    This function will initialize the cluster with unique vertex name as key of hashmap and
    in integer array it will initialize weight with 1 of every vertex and cluster id will be different initially
    */
    private void initializeCluster()
    {
        //making the set having unique vertices
        Set<String> unique= new HashSet();
        for (ArrayList x: graph.keySet())
        {
            unique.add(x.get(0).toString());
            unique.add(x.get(1).toString());
        }
        int count=1;
        for (String x: unique)
        {
            //0 index for weight and 1 index for id
            int[] arry= {1,count};
            cluster.put(x,arry);
            count++;
        }
    }

    /*This method will return the minimum weight of edge that exist in the graph.
    This will use duplicate graph so that every time it can return new minimum weight.
    */
    private int findMin()
    {
        int min=0;
        for(ArrayList x:duplicateGraph.keySet())
        {
            min=duplicateGraph.get(x);
            break;
        }
        //finding the minimum value from the graph
        for(ArrayList x:duplicateGraph.keySet())
        {
            if(min>duplicateGraph.get(x))
            {
                min=duplicateGraph.get(x);
            }
        }
        return min;
    }


    /*This method is used to compare the value of strings passed from above methods this will give the greater value of the string which will be used at multiple methods.
     * This method will compare the string according to its ascii values*/
    private String checkValue(String vertex1,String vertex2)
    {
        //check min length between two strings
        int min=Math.min(vertex2.length(),vertex1.length());
        int count=0;

        //checking the strings are equal
        for(int i=0;i<min;i++)
        {
            if (vertex1.toUpperCase().charAt(i) == vertex2.toUpperCase().charAt(i)) {
                count++;
            }
            if(count==min){
                return null;
            }
        }
        //if not equal the loop will return greater string according to ascii value
        for (int i=0;i<vertex1.toUpperCase().charAt(i) && i<vertex2.toUpperCase().charAt(i);i++)
        {
            if(vertex1.toUpperCase().charAt(i)<vertex2.toUpperCase().charAt(i))
            {
                return vertex1;
            }
            else if(vertex1.toUpperCase().charAt(i)>vertex2.toUpperCase().charAt(i))
            {
                return vertex2;
            }
        }

        return null;
    }
}
