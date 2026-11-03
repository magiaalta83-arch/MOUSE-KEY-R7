package com.seu.meumouse

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast

class MeuServicoAcessibilidade : AccessibilityService() {

    private lateinit var windowManager: WindowManager
    private var botaoFlutuante: View? = null
    private var pontoToqueX = 500f
    private var pontoToqueY = 1000f

    override fun onServiceConnected() {
        super.onServiceConnected()
        Toast.makeText(this, "🎮 Serviço ativado!", Toast.LENGTH_SHORT).show()
        criarJanelaFlutuante()
    }

    private fun criarJanelaFlutuante() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        
        val layoutTipo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        botaoFlutuante = LayoutInflater.from(this).inflate(R.layout.botao_flutuante, null)
        
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutTipo,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.START
        params.x = 100
        params.y = 500

        windowManager.addView(botaoFlutuante, params)

        val botao = botaoFlutuante?.findViewById<Button>(R.id.botaoAcao)
        botao?.setOnClickListener {
            simularToque(pontoToqueX, pontoToqueY)
        }

        var inicialX = 0
        var inicialY = 0
        botao?.setOnTouchListener { _, evento ->
            when (evento.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    inicialX = evento.rawX.toInt() - params.x
                    inicialY = evento.rawY.toInt() - params.y
                    true
                }
                android.view.MotionEvent.ACTION_MOVE -> {
                    params.x = evento.rawX.toInt() - inicialX
                    params.y = evento.rawY.toInt() - inicialY
                    pontoToqueX = params.x.toFloat() + 40f
                    pontoToqueY = params.y.toFloat() + 40f
                    windowManager.updateViewLayout(botaoFlutuante, params)
                    true
                }
                else -> false
            }
        }
    }

    fun simularToque(x: Float, y: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val caminho = Path()
            caminho.moveTo(x, y)
            val gesto = GestureDescription.Builder()
                .addStroke(GestureDescription.StrokeDescription(caminho, 0, 100))
                .build()
            dispatchGesture(gesto, null, null)
        }
    }

    override fun onAccessibilityEvent(event: android.view.accessibility.AccessibilityEvent?) {}
    override fun onInterrupt() {
        if (botaoFlutuante != null) windowManager.removeView(botaoFlutuante)
    }
}
