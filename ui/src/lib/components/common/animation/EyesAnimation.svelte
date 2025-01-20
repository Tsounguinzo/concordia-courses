<script lang="ts">
    import {DotLottieSvelte} from '@lottiefiles/dotlottie-svelte';

    export let width: number = 30;
    export let height: number = 30;
    export let animated: boolean = true;
</script>

<div style="width: {width}px; height: {height}px;">
    <DotLottieSvelte
            src="https://assets-v2.lottiefiles.com/a/c309332e-1167-11ee-bf04-6f799f1ab558/RkP0mjBFUq.lottie"
            autoplay={animated}
            loop={animated}
    />
</div>