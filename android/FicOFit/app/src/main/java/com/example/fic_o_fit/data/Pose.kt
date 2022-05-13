package com.example.fic_o_fit.data

class Pose(
    var id: Int = -1, // default id is -1
    val keypoints: List<Keypoint>,
    val score: Float
)