package com.example.simmone.view.activities

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.unit.dp

class DrawingActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyDrawing()
        }
    }
}
@Composable
fun MyDrawing() {
    val actionIdle = 0
    val actionDown = 1
    val actionMove = 2
    val actionUp = 3
    //Path, current touch position and touch states
    val path = remember { Path() }
    val rect = remember { Rect() }
    var motionEvent by remember { mutableStateOf(actionIdle) }
    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }
    val drawModifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .background(Color.Black)
        .clipToBounds()
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {

                    // Wait for at least one pointer to press down, and set first contact position
                    val down: PointerInputChange = awaitFirstDown().also {
                        motionEvent = actionDown
                        currentPosition = it.position
                    }
                    do {
                        // This PointerEvent contains details including events, id, position and more
                        val event: PointerEvent = awaitPointerEvent()

                        var eventChanges =
                            "DOWN changedToDown: ${down.changedToDown()} changedUp: ${down.changedToUp()}\n"
                        event.changes
                            .forEachIndexed { index: Int, pointerInputChange: PointerInputChange ->
                                eventChanges += "Index: $index, id: ${pointerInputChange.id}, " +
                                        "changedUp: ${pointerInputChange.changedToUp()}" +
                                        "pos: ${pointerInputChange.position}\n"

                                pointerInputChange.consumePositionChange()
                            }

                        motionEvent = actionMove
                        currentPosition = event.changes.first().position
                    } while (event.changes.any { it.pressed })

                    motionEvent = actionUp
                }
            }

        }


    Canvas(
        modifier = drawModifier
            .padding(20.dp)
            .size(500.dp)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val line = 1.5
        val squareSize = canvasWidth/line

        drawRect(
            color = Color.LightGray,
            topLeft = Offset(center.x - canvasWidth / 3, center.y - canvasHeight / 6),
            size = Size(width = squareSize.toFloat(), squareSize.toFloat()),
            style = Stroke(
                width = 50.dp.toPx()
            ),
        )


        when(motionEvent){
            actionDown->{
                path.moveTo(currentPosition.x,currentPosition.y)
            }
            actionMove->{
                    path.lineTo(currentPosition.x,currentPosition.y)
            }
            actionUp->{
                path.lineTo(currentPosition.x,currentPosition.y)
                motionEvent = actionIdle

                val pathOfDrawing = path.getBounds()
                Log.d("path1",pathOfDrawing.toString())

                val pathOfShape =rect.contains(pathOfDrawing.left.toInt(),
                    pathOfDrawing.top.toInt(), pathOfDrawing.right.toInt(),
                    pathOfDrawing.bottom.toInt())

                Log.d("path2",pathOfShape.toString())

            }
            else-> Unit
        }

       drawPath(
           color = Color.Cyan,
           path = path,
           style = Stroke(width = 5.dp.toPx(), join = StrokeJoin.Round)
       )

    }

}