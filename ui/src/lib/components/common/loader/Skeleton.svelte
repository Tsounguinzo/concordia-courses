<script lang="ts">
  //npm i skeleton-elements --save
    import {multiplySvgPoints} from "$lib/utils";

    export let width: number = 1200;
    export let height: number = 400;

    export let color: string;
    export let iconColor: string;
    export let showIcon: boolean = false;
    export let effect: 'fade' | 'pulse' | 'blink' | 'wave' = 'wave';
    export let borderRadius: string;

    export let count: number = 10;

    let rest = {};
    $: Object.keys($$props).forEach((prop) => {
        if (
            prop !== 'width'
            && prop !== 'height'
            && prop !== 'color'
            && prop !== 'iconColor'
            && prop !== 'showIcon'
            && prop !== 'effect'
            && prop !== 'class'
        ) {
            rest[prop] = $$props[prop];
        }
    });

    $: skeletonClassName = [
        'skeleton-image',
        effect && `skeleton-effect-${effect}`,
      $$props.class
    ]
        .filter(c => !!c)
        .join(' ');

    function multiplyPoints(pointsString) {
        return multiplySvgPoints(pointsString, 56, width, height);
    }

</script>
{#each Array(count) as i}
<span
        {...rest}
        class={skeletonClassName}
>
  <svg
          xmlns="http://www.w3.org/2000/svg"
          width={width}
          height={height}
          viewBox={`0 0 ${width} ${height}`}
          preserveAspectRatio="none"
          style={borderRadius ? `border-radius: ${borderRadius}` : ''}
  >
    <polygon
            style={color ? `fill: ${color}` : ''}
            fillRule="evenodd"
            points={`0 0 ${width} 0 ${width} ${height} 0 ${height}`}
    />
      {#if showIcon}
      <path style={iconColor ? `fill: ${iconColor}` : ''}
            d={multiplyPoints('M7.7148,49.5742 L48.2852,49.5742 C53.1836,49.5742 55.6446,47.1367 55.6446,42.3086 L55.6446,13.6914 C55.6446,8.8633 53.1836,6.4258 48.2852,6.4258 L7.7148,6.4258 C2.8398,6.4258 0.3554,8.8398 0.3554,13.6914 L0.3554,42.3086 C0.3554,47.1602 2.8398,49.5742 7.7148,49.5742 Z M39.2851,27.9414 C38.2304,27.0039 37.0351,26.5118 35.7695,26.5118 C34.457,26.5118 33.3085,26.9571 32.2304,27.918 L21.6366,37.3867 L17.3007,33.4492 C16.3163,32.582 15.2617,32.1133 14.1366,32.1133 C13.1054,32.1133 12.0976,32.5586 11.1366,33.4258 L4.1288,39.7305 L4.1288,13.8789 C4.1288,11.4414 5.4413,10.1992 7.7851,10.1992 L48.2147,10.1992 C50.535,10.1992 51.8708,11.4414 51.8708,13.8789 L51.8708,39.7539 L39.2851,27.9414 Z M17.8163,28.1992 C20.8398,28.1992 23.3241,25.7149 23.3241,22.668 C23.3241,19.6445 20.8398,17.1367 17.8163,17.1367 C14.7695,17.1367 12.2851,19.6445 12.2851,22.668 C12.2851,25.7149 14.7695,28.1992 17.8163,28.1992 Z')}/>
    {/if}
  </svg>
  <slot/>
</span>
{/each}
<style>
    .skeleton-image {
        display: inline-block;
    }

    .skeleton-image svg {
        max-width: 100%;
        height: auto;
    }

    .skeleton-image polygon {
        fill: var(--skeleton-color);
    }

    .skeleton-image path {
        fill: var(--skeleton-icon-color);
    }

    .skeleton-effect-blink, .skeleton-effect-wave {
        -webkit-mask-image: linear-gradient(to right, transparent 0%, black 25%, black 75%, transparent 100%);
        mask-image: linear-gradient(to right, transparent 0%, black 25%, black 75%, transparent 100%);
        -webkit-mask-size: 200% 100%;
        mask-size: 200% 100%;
        -webkit-mask-repeat: repeat;
        mask-repeat: repeat;
        -webkit-mask-position: 50% top;
        mask-position: 50% top;
        animation: skeleton-effect-wave 2s infinite;
    }

    .skeleton-effect-fade {
        animation: skeleton-effect-fade 2s infinite;
    }

    .skeleton-effect-pulse {
        animation: skeleton-effect-pulse 2s infinite;
    }

    @keyframes skeleton-effect-fade {
        0% {
            opacity: 1;
        }
        50% {
            opacity: 0.2;
        }
        100% {
            opacity: 1;
        }
    }

    @keyframes skeleton-effect-wave {
        0% {
            -webkit-mask-position: 50% top;
            mask-position: 50% top;
        }
        100% {
            -webkit-mask-position: -150% top;
            mask-position: -150% top;
        }
    }

    @keyframes skeleton-effect-pulse {
        0% {
            transform: scale(1);
        }
        40% {
            transform: scale(1);
        }
        50% {
            transform: scale(0.975);
        }
        100% {
            transform: scale(1);
        }
    }
</style>