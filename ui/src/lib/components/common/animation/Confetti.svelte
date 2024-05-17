<script lang="ts">
    import {ConfettiP5} from 'svelte-confetti-p5';
    import type {Writable} from "svelte/store";
    import {writable} from "svelte/store";

    export let trigger: Writable<boolean> = writable(false);
    export let amount: number = 300;
    export let colors: string[] = [
        "#f44336", "#e91e63", "#9c27b0", "#673ab7", "#3f51b5",
        "#2196f3", "#03a9f4", "#00bcd4", "#009688", "#4CAF50",
        "#8BC34A", "#CDDC39", "#FFEB3B", "#FFC107", "#FF9800",
        "#FF5722"
    ];
    export let minSize: number = 5;
    export let maxSize: number = 15;
    export let loop: boolean = false;
    export let spacing: number = 10;
    export let power: number = 10;
    export let shapes: "squares" | "circles" | "mix" | "images" = "mix";
    export let images: string[] = [];
    export let wind: number = 6;
    export let weight: number = 100;
    export let rotate: number = 2;
    export let flip: number = 20;
    export let w: number;
    export let h: number;
    export let position: "fixed" | "absolute" = "fixed";
    export let zIndex: number = 999;
    export let destoryOnFinish: boolean = true;
    export let frameRate: number = 60;
    let show: Writable<boolean> = writable(false);
    let replay: number = 0;

    $: if ($trigger) {
        replay++;
        show.set(true);
    }
    const onEachFall = () => {
    };
    const onStart = () => {
        trigger.set(false);
    };
    const onDone = () => {
        show.set(false);
    };
    const onDestroy = () => {
    };

    $: {
        w = window.innerWidth;
        h = window.innerHeight;
    }

</script>
{#key replay}
    {#if $show}
    <ConfettiP5
            on:eachfall={onEachFall}
            on:start={onStart}
            on:done={onDone}
            on:destroy={onDestroy}
            {amount}
            {colors}
            {minSize}
            {maxSize}
            {loop}
            {spacing}
            {power}
            {shapes}
            {images}
            {wind}
            {weight}
            {rotate}
            {flip}
            {w}
            {h}
            {position}
            {zIndex}
            {destoryOnFinish}
            {frameRate}
    />
        {/if}
{/key}

<style>
    .confetti-p5-container {
        position: fixed;
        width: 100vw;
        height: 100vh;
        top: 0;
        left: 0;
        pointer-events: none;
    }
</style>