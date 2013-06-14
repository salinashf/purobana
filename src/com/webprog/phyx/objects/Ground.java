package com.webprog.phyx.objects;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.vecmath.Vector3f;

import android.content.Context;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.webprog.R;
import com.webprog.phyx.utils.Utils;

public class Ground implements GLObjectInterface {
	RigidBody mRigidBody;
	
	FloatBuffer mVertexBuffer;
	ByteBuffer mIndexBuffer;
	
	int mTexture, mTextureDark;
	int posBufferObject;
	
	public Ground(DynamicsWorld world) {
		createGeometry();
		createRigidBody();
		
		world.addRigidBody(mRigidBody);
	}

	private void createGeometry() {
		float vertices[] = {
			-1000.f, -1000.f, 0.f, 0.0f, 0.0f,
			-1000.f, 1000.f, 0.f, 0.0f, 200.0f,
			1000.f, -1000.f, 0.f, 200.0f, 0.0f,
			1000.f, 1000.f, 0.f, 200.0f, 200.0f,
		};
		
		byte indices[] = {
			0, 1, 2, 3,
		};
		
		mVertexBuffer = Utils.allocateFloatBuffer(vertices);
		mIndexBuffer = Utils.allocateByteBuffer(indices);
	}

	private void createRigidBody() {
		CollisionShape shape = new StaticPlaneShape(new Vector3f(0.f, 0.f, 1.f), 0);
		
		DefaultMotionState motionState = new DefaultMotionState();
		
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(0, motionState, shape);
		
		mRigidBody = new RigidBody(rbInfo);
	}
	
	@Override
	public void draw(GL10 gl) {
		gl.glPushMatrix();

		gl.glFrontFace(GL10.GL_CCW);
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		GL11 gl11 = (GL11) gl;
		{
			int buffers[] = new int[1];
			gl11.glGenBuffers(1, buffers, 0);
			posBufferObject = buffers[0];
			
			gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, posBufferObject);
			gl11.glBufferData(GL11.GL_ARRAY_BUFFER, mVertexBuffer.capacity() * 4, mVertexBuffer, GL11.GL_STATIC_DRAW);
		}
		{
			gl11.glVertexPointer(3, GL10.GL_FLOAT, 4 * 5, 0);
	        gl11.glTexCoordPointer(2, GL10.GL_FLOAT, 4 * 5, 4 * 3);
		}
		
		
		 gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
		
		gl.glEnable(GL11.GL_TEXTURE_2D);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture);
		
		{
			gl.glEnable(GL10.GL_BLEND);
			gl.glEnable(GL10.GL_ALPHA);
		
			gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		}
		
		gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
		
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		gl.glDisable(GL11.GL_TEXTURE_2D);
		
		gl.glPopMatrix();
	}
	
	
	@Override
	public void init(GL10 gl, Context context) {
		mTexture = Utils.returnTex(gl, context, R.drawable.ground3);
		//Utils.enableMaterial(gl);
	}
	
}