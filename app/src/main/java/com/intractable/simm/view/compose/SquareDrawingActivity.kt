package com.intractable.simm.view.compose

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.intractable.simm.R
import com.intractable.simm.view.activities.MainActivity
import com.smarttoolfactory.gesture.MotionEvent
import com.smarttoolfactory.gesture.pointerMotionEvents
import kotlinx.coroutines.delay
import java.io.Serializable

class SquareDrawingActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyDrawing()
        }
    }
}
@Composable
fun MyDrawing() {
    val context = LocalContext.current
    val composableScope = rememberCoroutineScope()
    // This is motion state. Initially or when touch is completed state is at MotionEvent.Idle
    // When touch is initiated state changes to MotionEvent.Down, when pointer is moved MotionEvent.Move,
    // after removing pointer we go to MotionEvent.Up to conclude drawing and then to MotionEvent.Idle
    // to not have undesired behavior when this composable recomposes. Leaving state at MotionEvent.Up
    // causes incorrect drawing.
    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }
    // This is our motion event we get from touch motion
    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }
    // This is previous motion event before next touch is saved into this current position
    var previousPosition by remember { mutableStateOf(Offset.Unspecified) }

    var threshold = 8000  // number of pixels (squared) threshold for checkpoints

    var gameState = 0  // 0 = initial, 1 = touched one, 2 = touched more than one
    var startingCheckpoint = 0  // index of starting checkpoint
    var previousCheckpoint = -1  // index of previous checkpoint
    var nextCheckpoint = 1  // index of next checkpoint
    var direction = -1  // the value to add to "nextCheckpoint" when hitting a checkpoint
    var gameSuccess = false  // whether the player has successfully finished the shape
    var gameFailed = false

    val innerPath = remember { Path() }
    val outerPath = remember { Path() }

    // Path is what is used for drawing line on Canvas
    val path = remember { Path() }

    var isError by remember { mutableStateOf(false) }


    val drawModifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)
        .padding(0.dp, 200.dp, 0.dp, 0.dp)
        .height(500.dp)
        .background(Color.Black)
        .pointerMotionEvents(
            onDown = { pointerInputChange: PointerInputChange ->
                currentPosition = pointerInputChange.position
                motionEvent = MotionEvent.Down
                pointerInputChange.consume()
            },
            onMove = { pointerInputChange: PointerInputChange ->
                currentPosition = pointerInputChange.position
                motionEvent = MotionEvent.Move
                pointerInputChange.consume()
            },
            onUp = { pointerInputChange: PointerInputChange ->
                motionEvent = MotionEvent.Up
                pointerInputChange.consume()
            },
            delayAfterDownInMillis = 25L
        )

    Canvas(modifier = drawModifier) {

        val canvasWidth = size.width
        val canvasHeight = size.height
        threshold = threshold / 720 * canvasWidth.toInt()  // scale threshold to any screen size

        val outerShapeWidth = canvasWidth * .65f
        val innerShapeWidth = canvasWidth * .50f

        if (innerPath.isEmpty) {

            innerPath.addRect(
                Rect(
                    offset = Offset(
                        (canvasWidth - innerShapeWidth) / 2,
                        (canvasHeight - innerShapeWidth) / 2
                    ),
                    size = Size(innerShapeWidth, innerShapeWidth)
                )
            )
        }
        if (outerPath.isEmpty) {
            outerPath.addRect(
                Rect(
                    offset = Offset(
                        (canvasWidth - outerShapeWidth) / 2,
                        (canvasHeight - outerShapeWidth) / 2
                    ),
                    size = Size(outerShapeWidth, outerShapeWidth)
                )
            )
        }

        val squareSize = .575f
        val checkpointPositionX = (canvasWidth - (canvasWidth * squareSize)) / 2
        val checkpointPositionY = (canvasHeight - (canvasWidth * squareSize)) / 2

        val checkpoint1 by mutableStateOf(Offset(checkpointPositionX, checkpointPositionY))
        val checkpoint2 by mutableStateOf(Offset(canvasWidth - checkpointPositionX, checkpointPositionY))
        val checkpoint3 by mutableStateOf(Offset(canvasWidth - checkpointPositionX, canvasHeight - checkpointPositionY))
        val checkpoint4 by mutableStateOf(Offset(checkpointPositionX, canvasHeight - checkpointPositionY))

        val checkPointList = mutableListOf<Offset>()
        checkPointList.add(checkpoint1)
        checkPointList.add(checkpoint2)
        checkPointList.add(checkpoint3)
        checkPointList.add(checkpoint4)

        //compare distance between 2 moving touch and checkpoints, Pythagoras without square root(x1-x2)^2 * (y1-y2)^2
        fun offsetDistanceSquared(coordinate1: Offset, coordinate2: Offset): Float{
            return (coordinate1.x - coordinate2.x) * (coordinate1.x - coordinate2.x) + (coordinate1.y - coordinate2.y) * (coordinate1.y - coordinate2.y)
        }

        fun checkCheckpoints(inputPosition: Offset) {

            if (gameState == 0) {
                // check all checkpoints
                // if a checkpoint is within threshold, move to next state and store index of checkpoint
                for (i in 0 until checkPointList.size - 1) {
                    if (offsetDistanceSquared(inputPosition,
                            checkPointList[i]) < threshold
                    ) {
                        Log.d("checkpoints", "game state 1")

                        gameState = 1
                        startingCheckpoint = i
                        previousCheckpoint = i - 1
                        nextCheckpoint = i + 1

                        if (previousCheckpoint < 0) previousCheckpoint = checkPointList.size - 1
                        if (nextCheckpoint > checkPointList.size - 1) nextCheckpoint = 0

                        Log.d("checkpoints_prev", previousCheckpoint.toString())
                        Log.d("checkpoints_next", nextCheckpoint.toString())
                        return
                    }
                }
            } else if (gameState == 1) {
                // check previous and next checkpoint
                // if a checkpoint is within threshold, move to next state and store index of next checkpoint
                if (offsetDistanceSquared(inputPosition,
                        checkPointList[previousCheckpoint]) < threshold
                ) {
                    Log.d("checkpoints", "game state 2")
                    gameState = 2
                    direction = -1
                    nextCheckpoint = previousCheckpoint + direction
                    if (nextCheckpoint < 0) nextCheckpoint = checkPointList.size - 1


                    Log.d("checkpoints_next", nextCheckpoint.toString())
                }
                else if (offsetDistanceSquared(inputPosition,
                        checkPointList[nextCheckpoint]) < threshold
                ) {
                    Log.d("checkpoints", "game state 2")
                    gameState = 2
                    direction = 1

                    nextCheckpoint += direction
                    if (nextCheckpoint > checkPointList.size - 1) nextCheckpoint = 0


                    Log.d("checkpoints_next", nextCheckpoint.toString())
                }
            }
            else{  // gamestate == 2
                // only check for next checkpoint
                // if you are within the threshold for the next checkpoint, store index of next checkpoint
                    // then check if current checkpoint is starting checkpoint
                            // success
                if (offsetDistanceSquared(inputPosition,
                        checkPointList[nextCheckpoint]) < threshold
                ) {
                    Log.d("checkpoints", "reached another checkpoint")

                    if(nextCheckpoint == startingCheckpoint){
                        Log.d("checkpoints", "success")
                        gameSuccess = true
                        gameState = 3
                        context.startActivity(Intent(context, MainActivity::class.java))

                    }

                    nextCheckpoint += direction
                    if (nextCheckpoint < 0) nextCheckpoint = checkPointList.size - 1
                    if (nextCheckpoint > checkPointList.size - 1) nextCheckpoint = 0


                    Log.d("checkpoints_next", nextCheckpoint.toString())
                }
            }
        }


        when (motionEvent) {
            MotionEvent.Down -> {
                path.moveTo(currentPosition.x, currentPosition.y)
                previousPosition = currentPosition
                isError = !isInBound(innerPath = innerPath, outerPath = outerPath, currentPosition)
            }

            MotionEvent.Move -> {
                if(previousPosition.x == 0f && previousPosition.y == 0f) previousPosition = currentPosition
                path.quadraticBezierTo(
                    previousPosition.x,
                    previousPosition.y,
                    (currentPosition.x) ,
                    (currentPosition.y)

                )
                previousPosition = currentPosition
                isError = !isInBound(innerPath = innerPath, outerPath = outerPath, currentPosition)
                if(isError){
                    path.reset()
                    gameFailed = true
                }

                Log.d("touchDistanceCP1", offsetDistanceSquared(currentPosition, checkpoint1).toString())

                checkCheckpoints(currentPosition)
            }

            MotionEvent.Up -> {
                path.lineTo(currentPosition.x, currentPosition.y)
                currentPosition = Offset.Unspecified
                previousPosition = currentPosition
                motionEvent = MotionEvent.Idle
                if(!gameSuccess){
                    gameFailed = true
                    path.reset()
                    gameState = 0
                }
            }

            else -> Unit
        }

        drawPath(color = Color.LightGray, path = outerPath)
        drawPath(color = Color.Black, path = innerPath)


        drawPath(
            color = Color.Red,
            path = path,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        drawCircle(
            color = if(gameSuccess) Color.Green else if(gameFailed) Color.Red else Color.Yellow,
            center = Offset(100f, 100f),
            radius = 50f
        )
        drawCircle(
            color = Color.Cyan,
            center = checkpoint1,
            radius = 90f
        )
        drawCircle(
            color = Color.Cyan,
            center = checkpoint2,
            radius = 90f
        )
        drawCircle(
            color = Color.Cyan,
            center = checkpoint3,
            radius = 90f
        )
        drawCircle(
            color = Color.Cyan,
            center = checkpoint4,
            radius = 90f
        )

        }
    Box(modifier = Modifier
        .padding(0.dp, 30.dp,20.dp,10.dp)
        ){
        Text(text = "Trace over this square without going outside", color = Color.White, fontSize = 25.sp, maxLines = 2)
    }

    }

private fun isInBound(innerPath: Path, outerPath: Path, position: Offset): Boolean {
    val innerRect = innerPath.getBounds()
    val outerRect = outerPath.getBounds()

    return !innerRect.contains(position) && outerRect.contains(position)
}

    //val context = LocalContext.current
//    context.startActivity(Intent(context, MainActivity::class.java))
//


