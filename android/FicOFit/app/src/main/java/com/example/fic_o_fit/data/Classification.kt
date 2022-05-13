package com.example.fic_o_fit.data

class Classification(
    var id: String = "",
    var title: String = "",
    var confidence: Float = 0F
) {
    override fun toString(): String {
        return "Title = $title, Confidence = $confidence)"
    }
}