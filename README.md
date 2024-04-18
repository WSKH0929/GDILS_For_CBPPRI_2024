# Instances and results of the experiments in Wang et al. (2024)
### Full reference: Sunkanghong Wang, Runqin Wang, Hao Zhang, Qiang Liu and Lijun Wei. (2024). A Goal-Driven Iterated Local Search Approach Based on the Maximal-Space Based Heuristic for the Circle Bin Packing Problem with Rectangular Items. Under revision.

This repository hosts the files backing up the data and experimental results described in our paper A Goal-Driven Iterated Local Search Approach Based on the Maximal-Space Based Heuristic for the Circle Bin Packing Problem with Rectangular Items referenced above. The paper is now under revision. A link to it will be provided as soon as possible.

If you have any questions, please feel free to reach out to **[villagerwei@gdut.edu.cn](mailto:villagerwei@gdut.edu.cn)** or **[wskh0929@gmail.com](mailto:wskh0929@gmail.com)**

## Structure

The main structure of this repository is displayed in a tree shape as follows.

```shell
├── instances
│   ├── existing
│   └── new
└── results
    ├── CBPP-RI
    │   ├── Cplex
    │   │   └── existing
    │   │       ├── images
    │   │       └── res
    │   └── GDILS
    │       ├── existing
    │       │   ├── 600s
    │       │   │   ├── images
    │       │   │   └── res
    │       │   └── 60s
    │       │       ├── images
    │       │       └── res
    │       └── new
    │           ├── 600s
    │           │   ├── images
    │           │   └── res
    │           └── 60s
    │               ├── images
    │               └── res
    └── SCPP-RI
        ├── max_area
        │   ├── best_res
        │   └── images
        └── max_num
            ├── best_res
            └── images
```

## Instances

The instances in the ``instances/existing`` folder are existing instances created by [López and Beasley (2018)]([Packing unequal rectangles and squares in a fixed size circular container using formulation space search - ScienceDirect](https://www.sciencedirect.com/science/article/abs/pii/S0305054818300509)) and [Bouzid and
Salhi (2020)]([Packing rectangles into a fixed size circular container: Constructive and metaheuristic search approaches - ScienceDirect](https://www.sciencedirect.com/science/article/abs/pii/S0377221720302149))
