<template>
    <div class="page">
        <video class="bg-video" autoplay muted loop>
      <source src="@/assets/Background.mp4" type="video/mp4" />
    </video>
    <div class="welcome-page">
        <div class="welcome-card">
            <h1 class="title">Welcome to <span class="brand">NoteFlow</span></h1>
            <p class="subtitle">A clean, simple and powerful place for your notes & tasks.</p>
            <router-link to="/auth" class="start-btn">Get Started</router-link>
        </div>
    </div>

    </div>

</template>

<style scoped>
*{
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

.bg-video {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  opacity: 1;
  z-index: 0;
  pointer-events: none;
}

.welcome-page {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: calc(100vh - var(--nav-height));
    
    padding: 20px;
    color: #fff;
}


.welcome-card {
    background: rgba(255, 255, 255, 0.2);
    backdrop-filter: blur(10px);
    padding: 40px 60px;
    border-radius: 20px;
    text-align: center;
    box-shadow: 8px 8px 16px #d1d9e6, -8px -8px 16px #f9f9f9;
}


.title {
    font-size: 48px;
    font-weight: 800;
    margin-bottom: 10px;
}


.brand {
    color: #d70b26;
}


.subtitle {
    font-size: 18px;
    opacity: 0.9;
    margin-bottom: 30px;
}


.start-btn {
    display: inline-block;
    padding: 12px 28px;
    background: #a40a0a;
    color: #ffffff;
    border-radius: 12px;
    font-size: 18px;
    font-weight: 600;
    text-decoration: none;
    transition: 0.2s;
}


.start-btn:hover {
    transform: translateY(-3px);
    box-shadow: 0 6px 18px rgba(0,0,0,0.25);
}
</style>