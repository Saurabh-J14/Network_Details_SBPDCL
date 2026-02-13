package com.techLabs.nbpdcl.view.activity;

import static com.techLabs.nbpdcl.Utils.Config.networkIdList;
import static com.techLabs.nbpdcl.Utils.ResponseDataUtils.changeBgTransparentBitmapColor;
import static com.techLabs.nbpdcl.Utils.ResponseDataUtils.changeBitmapColor;
import static com.techLabs.nbpdcl.Utils.ResponseDataUtils.drawableToBitmap;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.techLabs.nbpdcl.R;
import com.techLabs.nbpdcl.Utils.Args;
import com.techLabs.nbpdcl.Utils.BitmapImg;
import com.techLabs.nbpdcl.Utils.Config;
import com.techLabs.nbpdcl.Utils.FusedLocationProviderWrapper;
import com.techLabs.nbpdcl.Utils.MapLayer;
import com.techLabs.nbpdcl.Utils.OTFToBitmapConverter;
import com.techLabs.nbpdcl.Utils.PrefManager;
import com.techLabs.nbpdcl.Utils.ProgressBarLayout;
import com.techLabs.nbpdcl.Utils.ResponseDataUtils;
import com.techLabs.nbpdcl.Utils.callBack.AddDevice;
import com.techLabs.nbpdcl.Utils.callBack.LoadAllocationArgument;
import com.techLabs.nbpdcl.Utils.callBack.LoadFlowArgument;
import com.techLabs.nbpdcl.Utils.callBack.ShortCircuitArgument;
import com.techLabs.nbpdcl.databinding.ActivityMapBinding;
import com.techLabs.nbpdcl.databinding.NodePopLayoutBinding;
import com.techLabs.nbpdcl.models.ConsumerModel;
import com.techLabs.nbpdcl.models.Continent;
import com.techLabs.nbpdcl.models.DType;
import com.techLabs.nbpdcl.models.DeleteFeature;
import com.techLabs.nbpdcl.models.DeviceName;
import com.techLabs.nbpdcl.models.FindDeviceModel;
import com.techLabs.nbpdcl.models.Topology;
import com.techLabs.nbpdcl.models.analysis.LoadAllocationModel;
import com.techLabs.nbpdcl.models.analysis.LoadFlowModel;
import com.techLabs.nbpdcl.models.analysis.ShortCircuitModel;
import com.techLabs.nbpdcl.models.trace.Tracing;
import com.techLabs.nbpdcl.models.zoom.ZoomToLayer;
import com.techLabs.nbpdcl.retrofit.ApiInterface;
import com.techLabs.nbpdcl.retrofit.RetrofitClient;
import com.techLabs.nbpdcl.view.LayerInfo.DeviceInfo.BreakerSnippet;
import com.techLabs.nbpdcl.view.LayerInfo.DeviceInfo.FuseSnippet;
import com.techLabs.nbpdcl.view.LayerInfo.DeviceInfo.ShuntCapacitorSnippet;
import com.techLabs.nbpdcl.view.LayerInfo.DeviceInfo.SourceDialog;
import com.techLabs.nbpdcl.view.LayerInfo.DeviceInfo.SpotLoadSnippet;
import com.techLabs.nbpdcl.view.LayerInfo.DeviceInfo.SwitchSnippet;
import com.techLabs.nbpdcl.view.LayerInfo.DeviceInfo.TransformerSnippet;
import com.techLabs.nbpdcl.view.LayerInfo.LineInfo.CableSnippet;
import com.techLabs.nbpdcl.view.LayerInfo.LineInfo.OverheadSnippet;
import com.techLabs.nbpdcl.view.LayerInfo.LineInfo.UnbalanceSnippet;
import com.techLabs.nbpdcl.view.fragment.LoadFlowStatus;
import com.techLabs.nbpdcl.view.fragment.loadAllocations.LoadAllocation;
import com.techLabs.nbpdcl.view.fragment.loadFlowAnalysis.LoadFlow;
import com.techLabs.nbpdcl.view.fragment.loadFlowAnalysis.LoadFlowBox;
import com.techLabs.nbpdcl.view.fragment.shortCircuitAnalysis.ShortCircuit;
import com.techLabs.nbpdcl.view.fragment.shortCircuitAnalysis.ShortCircuitBox;
import com.techLabs.nbpdcl.view.survey.SectionDeviceDialog;
import com.techLabs.nbpdcl.view.survey.SourceEditDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.bonuspack.kml.KmlFeature;
import org.osmdroid.bonuspack.kml.KmlLineString;
import org.osmdroid.bonuspack.kml.KmlPlacemark;
import org.osmdroid.bonuspack.kml.KmlPoint;
import org.osmdroid.bonuspack.kml.KmlPolygon;
import org.osmdroid.bonuspack.kml.KmlTrack;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MapActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener, LoadFlowArgument, MapEventsReceiver, ShortCircuitArgument, LoadAllocationArgument, AddDevice {

    /* Survey */
    public static final int MULTIPLE_PERMISSIONS = 11;
    private static final int REQUEST_CODE_CHILD_ACTIVITY = 123;
    final boolean[] isExpanded = {false};
    private final List<String> caSectionList = new ArrayList<>();
    private final List<Polyline> CaPolylineList = new ArrayList<>();
    private final Map<String, Polyline> CaSectionId = new HashMap<>();
    private final ArrayList<DeviceName> mList = new ArrayList<>();
    private final ArrayList<Continent> continentList = new ArrayList<Continent>();
    private final List<String> ohSectionList = new ArrayList<>();
    private final List<String> spLineSectionList = new ArrayList<>();
    private final List<Polyline> ohPolylineList = new ArrayList<>();
    private final Map<String, Polyline> OhSectionId = new HashMap<>();
    private final List<Polyline> unBalPolylineList = new ArrayList<>();
    private final Map<String, Polyline> UnBalSectionId = new HashMap<>();
    private final List<Polyline> secNodeList = new ArrayList<>();
    private final Map<String, Polyline> secNodeSectionId = new HashMap<>();
    private final Map<String, Marker> breakerSectionId = new HashMap<>();
    private final List<Marker> breakerList = new ArrayList<>();
    private final Map<String, Marker> transformerSectionId = new HashMap<>();
    private final List<Marker> transformerList = new ArrayList<>();
    private final Map<String, Marker> fuseSectionId = new HashMap<>();
    private final List<Marker> fuseList = new ArrayList<>();
    private final Map<String, Marker> switchSectionId = new HashMap<>();
    private final List<Marker> switchedList = new ArrayList<>();
    private final Map<String, Marker> capacitorSectionId = new HashMap<>();
    private final List<Marker> capacitorList = new ArrayList<>();
    private final Map<String, Marker> spotLoadSectionId = new HashMap<>();
    private final List<Marker> spotLoadList = new ArrayList<>();
    private final Map<String, Marker> NodeId = new HashMap<>();
    private final Map<String, Polyline> loadFlowOverLoadSectionID = new HashMap<>();
    private final Map<String, Marker> loadFlowOverLoadDeviceID = new HashMap<>();
    private final Map<String, Polyline> loadFlowOverVoltageSectionID = new HashMap<>();
    private final Map<String, Marker> loadFlowOverVoltageDeviceID = new HashMap<>();
    private final Map<String, Polyline> loadFlowUnderVoltageSectionID = new HashMap<>();
    private final Map<String, Marker> loadFlowUnderVoltageDeviceID = new HashMap<>();
    private final Map<String, Polyline> shortCircuitRatingSectionID = new HashMap<>();
    private final Map<String, Marker> shortCircuitRatingDeviceID = new HashMap<>();
    private final Map<String, Polyline> shortCircuitOverLoadSectionID = new HashMap<>();
    private final Map<String, Marker> shortCircuitOverLoadDeviceID = new HashMap<>();
    private final Map<String, Polyline> shortCircuitOverVoltageSectionID = new HashMap<>();
    private final Map<String, Marker> shortCircuitOverVoltageDeviceID = new HashMap<>();
    private final Map<String, Polyline> shortCircuitUnderVoltageSectionID = new HashMap<>();
    private final Map<String, Marker> shortCircuitUnderVoltageDeviceID = new HashMap<>();
    private final List<String> loadFlowOverVoltageSectionId = new ArrayList<>();
    private final List<String> loadFlowUnderVoltageSectionId = new ArrayList<>();
    private final List<String> loadFlowOverLoadSectionId = new ArrayList<>();
    private final List<String> shortCircuitRatingSectionId = new ArrayList<>();
    private final List<String> shortCircuitOverVoltageSectionId = new ArrayList<>();
    private final List<String> shortCircuitUnderVoltageSectionId = new ArrayList<>();
    private final List<String> shortCircuitOverLoadSectionId = new ArrayList<>();
    private final int delay = 200;
    private final List<GeoPoint> newSectionGeoPointList = new ArrayList<>();
    private final List<Marker> vertexList = new ArrayList<>();
    private final List<Polyline> newPolyLineList = new ArrayList<>();
    private final List<GeoPoint> coordinateList = new ArrayList<>();
    int selectedMapMenuId = R.id.openStreetMap_menu;
    private ActivityMapBinding binding;
    //cable
    private JSONObject CaObject;
    private KmlDocument CableKml = null;
    private FolderOverlay CableFolderOverLay = null;
    //Overhead
    private JSONObject OhObject;
    private KmlDocument OverHeadKml = null;
    private KmlDocument UnBalencedKml = null;
    private KmlDocument sectionNodeKml = null;
    private KmlDocument BreakarKml = null;
    private KmlDocument SwitchKml = null;
    private KmlDocument FuseKml = null;
    private KmlDocument TransformerKml = null;
    private KmlDocument ShuntCapacitorKml = null;
    private KmlDocument SpotloadKml = null;
    private KmlDocument NodeKml = null;
    private KmlDocument sourceKml = null;
    private GeoPoint sourcePoint;
    private FolderOverlay OverheadFolderOverLay = null;
    private FolderOverlay UnBalanceFolderOverLay = null;
    private FolderOverlay sectionFolderOverLay = null;
    private FolderOverlay CircuitBreakerOverLay = null;
    private FolderOverlay DistributionTransferOverLay = null;
    private FolderOverlay FuseOverLay = null;
    private FolderOverlay SwitchOverLay = null;
    private FolderOverlay ShuntCapacitorOverLay = null;
    private FolderOverlay SpotLoadOverLay = null;
    private FolderOverlay SourceOverLay = null;
    private FolderOverlay nodeOverLay = null;
    private JSONObject UnBalObject;
    private JSONObject SecNodeObject;
    private ExpandableDeviceAdapter adapter;
    private String networkId = null;
    private String nodeId = null;
    private LoadFlow loadFlow;
    private ShortCircuit shortCircuit;
    private String overVoltageColors = null;
    private String underVoltageColors = null;
    private String overloadColors = null;
    private String ratingColors = null;
    private PrefManager prefManager;
    private Marker previousSelectedDevice;
    private String deviceType;
    private int deviceLocation;
    private String highlightSectionID;
    private String highlightDeviceNumber;
    private Polyline previousSelectedSection;
    private String sectionType;
    private Marker previousLoadFlowDevice;
    private String loadFlowDeviceType;
    private String loadFlowDeviceId;
    private Polyline loadFlowPreviousSelectedSection;
    private String loadFlowSectionId;
    private Marker shortCircuitPreviousDevice;
    private String shortCircuitDeviceType;
    private String shortCircuitDeviceId;
    private Polyline shortCircuitPreviousSelectedSection;
    private String shortCircuitSectionId;
    private Boolean isTracing = false;
    private Intent intent;
    private String DelSectionId;
    private String DelDeviceNumber;
    private String DelDeviceType;
    private String DelNetworkId;
    private ProgressBarLayout progressBarLayout;
    private int overLoadCount;
    private int overVoltageCount;
    private int underVoltageCount;
    private int ratingCount;
    private String[] permissions;
    private String[] permissions1;
    private Location locationOver;
    private MyLocationNewOverlay locationOverlay;
    private MapEventsOverlay evOverlay;
    private Marker selectedNode;
    private String selectedNodeID;
    private Boolean isSource = false;
    private String newNetworkID = "";
    private List<String> loadFlowList = new ArrayList<>();
    private List<String> shortCircuitList = new ArrayList<>();
    private ArrayList<String> selectedFeeder = new ArrayList<>();
    private boolean isBounding = false;
    private boolean isTopology = false;
    private boolean isDrawCurrentLocationChecked = true;
    private boolean isDrawManuallyChecked = false;
    private FindDeviceModel findDeviceModel;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @SuppressLint({"ClickableViewAccessibility", "MissingInflatedId", "WrongViewCast", "NonConstantResourceId", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        binding = ActivityMapBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        progressBarLayout = binding.progressBarLayout;
        progressBarLayout.startAnimation(delay);
        progressBarLayout.setVisibility(View.GONE);
        prefManager = new PrefManager(MapActivity.this);
        Config.isLoadFlow = false;
        Config.isShortCircuit = false;
        Config.isLoadAllocation = false;
        binding.map.setTileSource(TileSourceFactory.MAPNIK);
        binding.map.getTileProvider().clearTileCache();
        binding.map.setBuiltInZoomControls(false);
        binding.map.setMultiTouchControls(true);
        binding.map.setUseDataConnection(true);
        binding.map.getOverlayManager().getTilesOverlay().setLoadingBackgroundColor(Color.TRANSPARENT);
        binding.map.getOverlayManager().getTilesOverlay().setLoadingLineColor(Color.TRANSPARENT);
        binding.map.getController().setZoom(14);
        GeoPoint center = new GeoPoint(19.075344279459458, 72.87758981236598);
        binding.map.getController().animateTo(center);
        binding.map.getController().setZoom(18);
        intent = getIntent();

        permissions = new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET};
        permissions1 = new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET};

        if (!checkPermission()) {
            Snackbar.make(binding.getRoot(), "Please Grand The Permission", Snackbar.LENGTH_INDEFINITE).show();
            return;
        }

        if (Objects.requireNonNull(intent.getStringExtra("Type")).equals("AddNetwork") || Objects.requireNonNull(intent.getStringExtra("Type")).equals("ExistNetwork")) {
            evOverlay = new MapEventsOverlay(this, this);
            FusedLocationProviderWrapper fusedProvider = new FusedLocationProviderWrapper(this);
            locationOverlay = new MyLocationNewOverlay(fusedProvider, binding.map) {
                @Override
                public void onLocationChanged(Location location, IMyLocationProvider source) {
                    super.onLocationChanged(location, source);
                    if (location != null) {
                        locationOver = location;
                    }
                }
            };

            locationOverlay.enableMyLocation();
            locationOverlay.enableFollowLocation();
            locationOverlay.isFollowLocationEnabled();
            locationOverlay.setDrawAccuracyEnabled(true);
            Drawable icon = ContextCompat.getDrawable(this, R.drawable.ic_locate_location); // your icon
            if (icon instanceof BitmapDrawable) {
                locationOverlay.setDirectionIcon(((BitmapDrawable) icon).getBitmap());
            }
            binding.map.getOverlays().add(locationOverlay);
            binding.map.invalidate();
            locationOverlay.runOnFirstFix(() -> {
                GeoPoint geoPoint = locationOverlay.getMyLocation();
                if (geoPoint != null) {
                    runOnUiThread(() -> {
                        binding.map.getController().animateTo(geoPoint);
                        binding.map.invalidate();
                    });
                }
            });
        }

        binding.mapLayer.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, v);
            popupMenu.getMenuInflater().inflate(R.menu.pop_menu, popupMenu.getMenu());
            popupMenu.getMenu().findItem(selectedMapMenuId).setChecked(true);
            try {
                Field popup = PopupMenu.class.getDeclaredField("mPopup");
                popup.setAccessible(true);
                Object menuPopupHelper = popup.get(popupMenu);
                assert menuPopupHelper != null;
                Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                Method setForceShowIcon = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                setForceShowIcon.invoke(menuPopupHelper, true);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        for (int i = 0; i < popupMenu.getMenu().size(); i++) {
                            popupMenu.getMenu().getItem(i).setChecked(false);
                        }
                        menuItem.setChecked(true);

                        selectedMapMenuId = menuItem.getItemId();

                        switch (menuItem.getItemId()) {
                            case R.id.noMap_menu:
                                binding.map.setTileSource(TileSourceFactory.OPEN_SEAMAP);
                                break;

                            case R.id.openStreetMap_menu:
                                binding.map.setTileSource(MapLayer.OpenStreetMap());
                                binding.map.invalidate();
                                break;

                            case R.id.googleMap_menu:
                                binding.map.setTileSource(MapLayer.RoadMap());
                                binding.map.invalidate();
                                break;

                            case R.id.hybridMap_menu:
                                binding.map.setTileSource(MapLayer.HybridMap());
                                binding.map.invalidate();
                                break;

                            default:
                                return false;
                        }

                        binding.map.invalidate();
                        return true;
                    }
                });
                popupMenu.show();
            } catch (Exception e) {
                Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
            }
        });

        binding.colorCodeView.setOnClickListener(v -> {
            Context wrapper = new ContextThemeWrapper(MapActivity.this, R.style.CustomPopupMenuStyle);
            PopupMenu popupMenu = new PopupMenu(wrapper, v);
            popupMenu.getMenuInflater().inflate(R.menu.color_code_menu, popupMenu.getMenu());
            popupMenu.setForceShowIcon(true);
            if (Config.isLoadFlow) {
                MenuItem menuItem1 = popupMenu.getMenu().getItem(0);
                menuItem1.setTitle("OverVoltage" + " (" + overVoltageCount + ")");
                Drawable icon1 = menuItem1.getIcon();
                if (overVoltageColors != null) {
                    if (icon1 != null) {
                        icon1.mutate();
                        icon1.setColorFilter(Color.parseColor(overVoltageColors), PorterDuff.Mode.SRC_IN);
                    }
                }

                MenuItem menuItem2 = popupMenu.getMenu().getItem(1);
                menuItem2.setTitle("UnderVoltage" + " (" + underVoltageCount + ")");
                Drawable icon2 = menuItem2.getIcon();
                if (underVoltageColors != null) {
                    if (icon2 != null) {
                        icon2.mutate();
                        icon2.setColorFilter(Color.parseColor(underVoltageColors), PorterDuff.Mode.SRC_IN);
                    }
                }

                MenuItem menuItem3 = popupMenu.getMenu().getItem(2);
                menuItem3.setTitle("Overload" + " (" + overLoadCount + ")");
                Drawable icon3 = menuItem3.getIcon();
                if (overloadColors != null) {
                    if (icon3 != null) {
                        icon3.mutate();
                        icon3.setColorFilter(Color.parseColor(overloadColors), PorterDuff.Mode.SRC_IN);
                    }
                }

                MenuItem menuItem4 = popupMenu.getMenu().getItem(3);
                menuItem4.setVisible(false);
                Drawable icon4 = menuItem4.getIcon();
                if (icon4 != null) {
                    icon4.mutate();
                    icon4.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
                }
            } else if (Config.isShortCircuit) {
                MenuItem menuItem1 = popupMenu.getMenu().getItem(0);
                menuItem1.setTitle("OverVoltage" + " (" + overVoltageCount + ")");
                Drawable icon1 = menuItem1.getIcon();
                if (overVoltageColors != null) {
                    if (icon1 != null) {
                        icon1.mutate();
                        icon1.setColorFilter(Color.parseColor(overVoltageColors), PorterDuff.Mode.SRC_IN);
                    }
                }

                MenuItem menuItem2 = popupMenu.getMenu().getItem(1);
                menuItem2.setTitle("UnderVoltage" + " (" + underVoltageCount + ")");
                Drawable icon2 = menuItem2.getIcon();
                if (underVoltageColors != null) {
                    if (icon2 != null) {
                        icon2.mutate();
                        icon2.setColorFilter(Color.parseColor(underVoltageColors), PorterDuff.Mode.SRC_IN);
                    }
                }

                MenuItem menuItem3 = popupMenu.getMenu().getItem(2);
                menuItem3.setTitle("Overload" + " (" + overLoadCount + ")");
                Drawable icon3 = menuItem3.getIcon();
                if (overloadColors != null) {
                    if (icon3 != null) {
                        icon3.mutate();
                        icon3.setColorFilter(Color.parseColor(overloadColors), PorterDuff.Mode.SRC_IN);
                    }
                }

                MenuItem menuItem4 = popupMenu.getMenu().getItem(3);
                menuItem4.setTitle("Rating" + " (" + ratingCount + ")");
                menuItem4.setVisible(true);
                Drawable icon4 = menuItem4.getIcon();
                if (ratingColors != null) {
                    if (icon4 != null) {
                        icon4.mutate();
                        icon4.setColorFilter(Color.parseColor(ratingColors), PorterDuff.Mode.SRC_IN);
                    }
                }
            } else {
                MenuItem menuItem1 = popupMenu.getMenu().getItem(0);
                menuItem1.setTitle("Cable");
                Drawable icon1 = menuItem1.getIcon();
                if (icon1 != null) {
                    icon1.mutate();
                    icon1.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                }

                MenuItem menuItem2 = popupMenu.getMenu().getItem(1);
                menuItem2.setTitle("OverHead");
                Drawable icon2 = menuItem2.getIcon();
                if (icon2 != null) {
                    icon2.mutate();
                    icon2.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
                }

                MenuItem menuItem3 = popupMenu.getMenu().getItem(2);
                menuItem3.setTitle("UnBalanced");
                Drawable icon3 = menuItem3.getIcon();
                if (icon3 != null) {
                    icon3.mutate();
                    icon3.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
                }

                MenuItem menuItem4 = popupMenu.getMenu().getItem(3);
                menuItem4.setVisible(false);
                Drawable icon4 = menuItem4.getIcon();
                if (icon4 != null) {
                    icon4.mutate();
                    icon4.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
                }
            }
            popupMenu.show();
        });

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        binding.search.setSearchableInfo(searchManager.getSearchableInfo(MapActivity.this.getComponentName()));
        binding.search.setIconifiedByDefault(false);
        binding.search.setOnQueryTextListener(this);
        binding.search.setOnCloseListener(this);
        @SuppressLint("DiscouragedApi")
        int id = binding.search.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = binding.search.findViewById(id);
        textView.setTextColor(Color.BLACK);
        textView.setHintTextColor(Color.BLACK);

        binding.drawerBtn.setColorFilter(getColor(R.color.blue));
        binding.searchIcon.setColorFilter(getColor(R.color.blue));
        binding.zoomOut.setColorFilter(getColor(R.color.blue));
        binding.zoomIn.setColorFilter(getColor(R.color.blue));
        binding.downTracing.setColorFilter(Color.MAGENTA);
        binding.upTracing.setColorFilter(Color.CYAN);
        binding.locationMarker.setColorFilter(getColor(R.color.blue));
        binding.analysisImgBtn.setColorFilter(getColor(R.color.blue));
        binding.mapLayer.setColorFilter(getColor(R.color.blue));
        binding.reportBtn.setColorFilter(getColor(R.color.blue));
        binding.reportBtn.setVisibility(View.GONE);
        binding.newConnectionLoadFlowStatusImgBtn.setColorFilter(getColor(R.color.blue));
        binding.deleteBtn.setColorFilter(getColor(R.color.blue));
        binding.colorCodeView.setColorFilter(getColor(R.color.blue));
        binding.locateLocation.setColorFilter(getColor(R.color.blue));
        binding.addNewNode.setColorFilter(getColor(R.color.blue));
        binding.drawLine.setColorFilter(getColor(R.color.blue));
        binding.addSource.setColorFilter(getColor(R.color.blue));
        binding.drawOption.setColorFilter(getColor(R.color.blue));
        binding.surveyDrawerBtn.setColorFilter(getColor(R.color.blue));
        binding.deleteSectionSurveyBtn.setColorFilter(getColor(R.color.blue));
        binding.surveyZoomIn.setColorFilter(getColor(R.color.blue));
        binding.surveyZoomOut.setColorFilter(getColor(R.color.blue));
        binding.toolsToggleBtn.setColorFilter(getColor(R.color.white));

        binding.surveyZoomIn.setOnClickListener(v -> {
            binding.map.getController().zoomIn();
            binding.map.invalidate();
        });

        binding.surveyZoomOut.setOnClickListener(v -> {
            binding.map.getController().zoomOut();
            binding.map.invalidate();
        });

        binding.drawerBtn.setOnClickListener(view -> {
            //binding.drawerLayout.openDrawer(GravityCompat.START);
            if (!binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.rLayout.animate()
                        .translationX(100f)
                        .scaleX(0.92f)
                        .scaleY(0.92f)
                        .setDuration(350)
                        .setInterpolator(new android.view.animation.OvershootInterpolator())
                        .start();

                binding.drawerLayout.post(() -> {
                    binding.drawerLayout.openDrawer(GravityCompat.START);
                    View drawerView = binding.drawerLayout.findViewById(R.id.navigationmenu);
                    if (drawerView != null) {
                        drawerView.setAlpha(0f);
                        drawerView.setTranslationX(-50f);
                        drawerView.animate()
                                .alpha(1f)
                                .translationX(0f)
                                .setDuration(300)
                                .setInterpolator(new android.view.animation.DecelerateInterpolator())
                                .start();
                    }
                });
            } else {
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                binding.rLayout.animate()
                        .translationX(0f)
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(300)
                        .setInterpolator(new android.view.animation.DecelerateInterpolator())
                        .start();
            }
        });

        binding.drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {

            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                float scale = 1 - (0.08f * (float) Math.sin(slideOffset * Math.PI / 2));
                float translationX = 100f * slideOffset;
                binding.rLayout.setTranslationX(translationX);
                binding.rLayout.setScaleX(scale);
                binding.rLayout.setScaleY(scale);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                binding.rLayout.animate()
                        .translationX(0f)
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(250)
                        .setInterpolator(new android.view.animation.DecelerateInterpolator())
                        .start();

                View menuView = binding.navigationmenu;
                menuView.animate()
                        .alpha(0f)
                        .translationY(-30f)
                        .setDuration(200)
                        .withEndAction(() -> menuView.setVisibility(View.INVISIBLE))
                        .start();
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                binding.rLayout.animate()
                        .translationX(100f)
                        .scaleX(0.92f)
                        .scaleY(0.92f)
                        .setDuration(250)
                        .setInterpolator(new android.view.animation.DecelerateInterpolator())
                        .start();

                View menuView = binding.navigationmenu;
                menuView.setVisibility(View.VISIBLE);
                menuView.setAlpha(0f);
                menuView.setTranslationY(-50f);
                menuView.animate()
                        .alpha(1f)
                        .translationY(0f)
                        .setDuration(400)
                        .setInterpolator(new android.view.animation.OvershootInterpolator())
                        .start();
            }
        });

        binding.surveyDrawerBtn.setOnClickListener(view -> {
            binding.drawerLayout.openDrawer(GravityCompat.START);
        });

        if (Objects.requireNonNull(intent.getStringExtra("Type")).equals("AddNetwork")) {
            if (!ResponseDataUtils.isLocationEnabled(this)) {
                showEnabledLocationDialog();
                return;
            }
            binding.sideLLayout.setVisibility(View.GONE);
            binding.colorCodeView.setVisibility(View.GONE);
            binding.menuLayout.setVisibility(View.GONE);

            binding.surveyMenuLayout.setVisibility(View.VISIBLE);
            binding.surveyLLayout.setVisibility(View.VISIBLE);
            binding.locateLocation.setVisibility(View.VISIBLE);
            binding.addSource.setVisibility(View.VISIBLE);

            if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(MapActivity.this)) {
                getConsumerData();
            } else {
                Snackbar.make(binding.getRoot(), R.string.no_internet_connection, Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(MapActivity.this)) {
                            getConsumerData();
                        }
                    }
                }).show();
            }

        } else if (Objects.requireNonNull(intent.getStringExtra("Type")).equals("ExistNetwork")) {
            if (!ResponseDataUtils.isLocationEnabled(this)) {
                showEnabledLocationDialog();
                return;
            }

            binding.sideLLayout.setVisibility(View.GONE);
            binding.colorCodeView.setVisibility(View.GONE);
            binding.menuLayout.setVisibility(View.GONE);

            binding.addSource.setVisibility(View.GONE);
            binding.surveyMenuLayout.setVisibility(View.GONE);
            binding.surveyLLayout.setVisibility(View.GONE);
            binding.locateLocation.setVisibility(View.GONE);
            if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(MapActivity.this)) {
                if (intent.getStringArrayListExtra("NetworkId") != null) {
                    getSurveyNetworkData(intent.getStringArrayListExtra("NetworkId").get(0).trim());
                    getConsumerData();
                }
            } else {
                Snackbar.make(binding.getRoot(), R.string.no_internet_connection, Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(MapActivity.this)) {
                            if (intent.getStringArrayListExtra("NetworkId").get(0) != null) {
                                getSurveyNetworkData(intent.getStringArrayListExtra("NetworkId").get(0).trim());
                            }
                        }
                    }
                }).show();
            }
        } else {
            binding.sideLLayout.setVisibility(View.GONE);
            binding.colorCodeView.setVisibility(View.GONE);
            binding.menuLayout.setVisibility(View.GONE);

            binding.surveyMenuLayout.setVisibility(View.GONE);
            binding.surveyLLayout.setVisibility(View.GONE);
            binding.locateLocation.setVisibility(View.GONE);

           /* if (prefManager.getUserType().contains("View")) {
                binding.analysisImgBtn.setVisibility(View.GONE);
                binding.deleteBtn.setVisibility(View.GONE);
                binding.newConnectionLoadFlowStatusImgBtn.setVisibility(View.GONE);
            } else if (prefManager.getUserType().contains("Edit")) {
                binding.analysisImgBtn.setVisibility(View.VISIBLE);
                binding.deleteBtn.setVisibility(View.VISIBLE);
                binding.newConnectionLoadFlowStatusImgBtn.setVisibility(View.VISIBLE);
            } else if (prefManager.getUserType().contains("Analysis")) {
                binding.analysisImgBtn.setVisibility(View.VISIBLE);
                binding.deleteBtn.setVisibility(View.GONE);
                binding.newConnectionLoadFlowStatusImgBtn.setVisibility(View.GONE);
            } else {
                binding.deleteBtn.setVisibility(View.GONE);
                binding.newConnectionLoadFlowStatusImgBtn.setVisibility(View.GONE);
                binding.analysisImgBtn.setVisibility(View.VISIBLE);
            }*/

            if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(MapActivity.this)) {
                JsonArray jsonArray = new Gson().toJsonTree(intent.getStringArrayListExtra("NetworkId")).getAsJsonArray();
                Gson gson = new Gson();
                Type listType = new TypeToken<List<String>>() {
                }.getType();
                selectedFeeder = gson.fromJson(jsonArray, listType);
                if (selectedFeeder != null && !selectedFeeder.isEmpty() && selectedFeeder.get(0) != null) {
                    getNetworkData(selectedFeeder.get(0).trim());
                }
            } else {
                Snackbar.make(binding.getRoot(), R.string.no_internet_connection, Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(MapActivity.this)) {
                            if (selectedFeeder != null && !selectedFeeder.isEmpty() && selectedFeeder.get(0) != null) {
                                getNetworkData(intent.getStringArrayListExtra("NetworkId").get(0).trim());
                            }
                        }
                    }
                }).show();
            }
        }

        binding.toolsToggleBtn.setOnClickListener(v -> {
            if (isExpanded[0]) {
                binding.toolsContainer.animate()
                        .alpha(0f)
                        .scaleY(0.8f)
                        .translationY(-binding.toolsContainer.getHeight() / 3f)
                        .setDuration(300)
                        .setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator())
                        .withEndAction(() -> binding.toolsContainer.setVisibility(View.GONE))
                        .start();

                binding.toolsToggleBtn.animate()
                        .rotation(0f)
                        .setDuration(250)
                        .setInterpolator(new android.view.animation.DecelerateInterpolator())
                        .start();

                isExpanded[0] = false;

            } else {
                binding.toolsContainer.setVisibility(View.VISIBLE);
                binding.toolsContainer.setAlpha(0f);
                binding.toolsContainer.setScaleY(0.8f);
                binding.toolsContainer.setTranslationY(-binding.toolsContainer.getHeight() / 3f);

                binding.toolsContainer.animate()
                        .alpha(1f)
                        .scaleY(1f)
                        .translationY(0f)
                        .setDuration(300)
                        .setInterpolator(new android.view.animation.OvershootInterpolator()) // adds bounce feel
                        .start();

                binding.toolsToggleBtn.animate()
                        .rotation(180f)
                        .setDuration(250)
                        .setInterpolator(new android.view.animation.OvershootInterpolator())
                        .start();

                isExpanded[0] = true;
            }
        });

        binding.zoomIn.setOnClickListener(view -> {
            binding.map.getController().zoomIn();
            binding.map.invalidate();
        });

        binding.zoomOut.setOnClickListener(view -> {
            binding.map.getController().zoomOut();
            binding.map.invalidate();
        });

        binding.upTracing.setOnClickListener(view -> {
            if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(MapActivity.this)) {
                getUpTracingData("upStream");
            } else {
                Snackbar.make(binding.getRoot(), R.string.no_internet_connection, Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(MapActivity.this)) {
                            getUpTracingData("upStream");
                        }
                    }
                }).show();
            }
        });

        binding.downTracing.setOnClickListener(view -> {
            if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(MapActivity.this)) {
                getDownTracingData("DownStream");
            } else {
                Snackbar.make(binding.getRoot(), R.string.no_internet_connection, Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(MapActivity.this)) {
                            getDownTracingData("DownStream");
                        }
                    }
                }).show();
            }
        });

        binding.locationMarker.setOnClickListener(view -> {
            if (CableKml != null) {
                BoundingBox boundingBox = CableKml.mKmlRoot.getBoundingBox();
                binding.map.zoomToBoundingBox(boundingBox, true);
                binding.map.getBoundingBox().getCenter();
                binding.map.getController().setZoom(7.0);
                binding.map.getController().setCenter(boundingBox.getCenter());
                binding.map.zoomToBoundingBox(boundingBox.increaseByScale(1.3f), true);
                binding.map.invalidate();
            } else if (OverHeadKml != null) {
                BoundingBox boundingBox = OverHeadKml.mKmlRoot.getBoundingBox();
                binding.map.zoomToBoundingBox(boundingBox, true);
                binding.map.getBoundingBox().getCenter();
                binding.map.getController().setZoom(7.0);
                binding.map.getController().setCenter(boundingBox.getCenter());
                binding.map.zoomToBoundingBox(boundingBox.increaseByScale(1.3f), true);
                binding.map.invalidate();
            } else {
                binding.map.getController().animateTo(sourcePoint, 25.0, 0L);
                binding.map.getController().setCenter(sourcePoint);
                binding.map.invalidate();
            }
        });

        binding.map.setMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                return true;
            }

            @Override
            public boolean onZoom(ZoomEvent event) {

                if (binding.map.getZoomLevel() > 18) {
                    AddNode();
                }

                if (binding.map.getZoomLevel() < 18) {
                    RemoveNode();
                }

                if (binding.map.getZoomLevel() > 22) {
                    AddDevices();
                }

                if (binding.map.getZoomLevel() < 22) {
                    RemoveDevices();
                }

                binding.map.isShown();
                return true;
            }
        });

        binding.newConnectionLoadFlowStatusImgBtn.setOnClickListener(v -> {
            if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(this)) {
                LoadFlowStatus loadFlowStatus = new LoadFlowStatus(MapActivity.this, intent.getStringArrayListExtra("NetworkId"));
                loadFlowStatus.show(getSupportFragmentManager(), loadFlowStatus.getTag());
            } else {
                Snackbar.make(binding.getRoot(), R.string.no_internet_connection, Snackbar.LENGTH_SHORT)
                        .setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LoadFlowStatus loadFlowStatus = new LoadFlowStatus(MapActivity.this, intent.getStringArrayListExtra("NetworkId"));
                                loadFlowStatus.show(getSupportFragmentManager(), loadFlowStatus.getTag());
                            }
                        }).show();
            }
        });

        binding.searchIcon.setOnClickListener(v -> {
            int fullWidth = ((View) binding.searchView.getParent()).getWidth() - binding.searchIcon.getWidth();

            if (!isExpanded[0]) {
                binding.searchView.setVisibility(View.VISIBLE);
                ValueAnimator animator = ValueAnimator.ofInt(0, fullWidth);
                animator.setDuration(600);
                animator.setInterpolator(new OvershootInterpolator(1.2f));
                animator.addUpdateListener(animation -> {
                    int val = (Integer) animation.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = binding.searchView.getLayoutParams();
                    layoutParams.width = val;
                    binding.searchView.setLayoutParams(layoutParams);
                });

                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        binding.searchView.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.showSoftInput(binding.searchView, InputMethodManager.SHOW_IMPLICIT);
                        }
                    }
                });

                animator.start();
                isExpanded[0] = true;

            } else {
                ValueAnimator animator = ValueAnimator.ofInt(binding.searchView.getWidth(), 0);
                animator.setDuration(600);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.addUpdateListener(animation -> {
                    int val = (Integer) animation.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = binding.searchView.getLayoutParams();
                    layoutParams.width = val;
                    binding.searchView.setLayoutParams(layoutParams);
                });
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        binding.searchView.setVisibility(View.INVISIBLE);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(binding.searchView.getWindowToken(), 0);
                        }
                    }
                });
                animator.start();

                isExpanded[0] = false;
            }
        });

        binding.searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty()) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(binding.searchView.getWindowToken(), 0);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.analysisImgBtn.setOnClickListener(v -> {
            Context wrapper = new ContextThemeWrapper(this, R.style.CustomPopupMenuStyle);
            PopupMenu popupMenu = new PopupMenu(wrapper, v);
            popupMenu.getMenuInflater().inflate(R.menu.analysis_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.analysis_loadflow:
                        if (intent.getStringArrayListExtra("NetworkId") != null) {
                            loadFlow = new LoadFlow(MapActivity.this);
                            Bundle bundle1 = new Bundle();
                            bundle1.putStringArrayList("Network", intent.getStringArrayListExtra("NetworkId"));
                            loadFlow.setArguments(bundle1);
                            loadFlow.show(getSupportFragmentManager(), loadFlow.getTag());
                            return true;
                        }

                    case R.id.analysis_shortcircuit:
                        if (intent.getStringArrayListExtra("NetworkId") != null) {
                            shortCircuit = new ShortCircuit(MapActivity.this);
                            Bundle bundle = new Bundle();
                            bundle.putStringArrayList("Network", intent.getStringArrayListExtra("NetworkId"));
                            bundle.putString("Index", "0");
                            bundle.putString("NodeId", null);
                            shortCircuit.setArguments(bundle);
                            shortCircuit.show(getSupportFragmentManager(), shortCircuit.getTag());
                            return true;
                        }

                    case R.id.analysis_loadAllocation:
                        if (intent.getStringArrayListExtra("NetworkId") != null) {
                            LoadAllocation loadAllocation = new LoadAllocation(MapActivity.this);
                            Bundle bundle = new Bundle();
                            bundle.putStringArrayList("Network", intent.getStringArrayListExtra("NetworkId"));
                            loadAllocation.setArguments(bundle);
                            loadAllocation.show(getSupportFragmentManager(), loadAllocation.getTag());
                            return true;
                        }

                    default:
                        return false;
                }
            });
            popupMenu.show();
        });

        binding.reportBtn.setOnClickListener(view -> {
            if (Config.isLoadFlow) {
                if (intent.getStringArrayListExtra("NetworkId") != null) {
                    Intent intent1 = new Intent(this, Reports.class);
                    intent1.putStringArrayListExtra("NetworkId", intent.getStringArrayListExtra("NetworkId"));
                    intent1.putExtra("Type", "loadflow");
                    startActivityForResult(intent1, REQUEST_CODE_CHILD_ACTIVITY);
                }
            } else if (Config.isShortCircuit) {
                if (intent.getStringArrayListExtra("NetworkId") != null) {
                    Intent intent1 = new Intent(this, Reports.class);
                    intent1.putStringArrayListExtra("NetworkId", intent.getStringArrayListExtra("NetworkId"));
                    intent1.putExtra("Type", "shortcircuit");
                    startActivityForResult(intent1, REQUEST_CODE_CHILD_ACTIVITY);
                }
            } else {
                Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Analysis Failed!", Snackbar.LENGTH_LONG);
                snack.show();
            }
        });

        binding.deleteSectionSurveyBtn.setOnClickListener(v -> {
            if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(this)) {
                if (DelSectionId != null && DelDeviceNumber != null && DelDeviceType != null && DelNetworkId != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
                    builder.setMessage("Do you want to Delete Section ?");
                    builder.setTitle("Alert !");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", (dialog, which) -> {
                        progressBarLayout.setVisibility(View.VISIBLE);
                        progressBarLayout.setProcessText("Delete Section, Please Wait!");
                        getFeatures(DelSectionId);
                        dialog.cancel();
                    });
                    builder.setNegativeButton("No", (dialog, which) -> {
                        dialog.cancel();
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    Snackbar.make(binding.getRoot(), "Please Select Section!", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                Snackbar.make(binding.getRoot(), R.string.no_internet_connection, Snackbar.LENGTH_INDEFINITE)
                        .setAction("Retry", v1 -> {
                            progressBarLayout.setVisibility(View.VISIBLE);
                            progressBarLayout.setProcessText("Delete Section, Please Wait!");
                            getFeatures(DelSectionId);
                        }).show();
            }
        });

        binding.locateLocation.setOnClickListener(v -> {
            if (ResponseDataUtils.isLocationEnabled(MapActivity.this)) {
                if (locationOver != null) {
                    binding.map.getController().animateTo(new GeoPoint(locationOver.getLatitude(), locationOver.getLongitude()), 20.0d, 1000L);
                    binding.map.invalidate();
                }
            } else {
                showEnabledLocationDialog();
            }
        });

        binding.drawLine.setOnClickListener(v -> {
            if (newSectionGeoPointList != null && !newSectionGeoPointList.isEmpty() && newSectionGeoPointList.size() > 1 && selectedNodeID != null) {
                if (newNetworkID != null && !newNetworkID.isEmpty()) {
                    SectionDeviceDialog editDevice = new SectionDeviceDialog(MapActivity.this, newSectionGeoPointList, newNetworkID, selectedNodeID);
                    editDevice.setCancelable(false);
                    editDevice.show();
                } else {
                    SectionDeviceDialog editDevice = new SectionDeviceDialog(MapActivity.this, newSectionGeoPointList, intent.getStringArrayListExtra("NetworkId").get(0), selectedNodeID);
                    editDevice.setCancelable(false);
                    editDevice.show();
                }
            } else {
                Snackbar.make(binding.getRoot(), "Please Select The FromNode!", Snackbar.LENGTH_SHORT).show();
            }
        });

        binding.addNewNode.setOnClickListener(v -> {
            if (checkPermission()) {
                if (Objects.requireNonNull(intent.getStringExtra("Type")).contains("ExistNetwork")) {
                    if (!newSectionGeoPointList.isEmpty() && !coordinateList.isEmpty() && selectedNodeID != null) {
                        checkPermissions();
                    } else {
                        Snackbar.make(binding.getRoot(), "Please Select FromNode!", Snackbar.LENGTH_LONG).show();
                    }
                } else if (Objects.requireNonNull(intent.getStringExtra("Type")).contains("AddNetwork") && !isSource) {
                    Snackbar.make(binding.getRoot(), "Please Add Source!", Snackbar.LENGTH_LONG).show();
                } else {
                    if (!coordinateList.isEmpty() && !newSectionGeoPointList.isEmpty() && selectedNodeID != null) {
                        checkPermissions();
                    } else {
                        Snackbar.make(binding.getRoot(), "Please Select FromNode!", Snackbar.LENGTH_LONG).show();
                    }
                }
            }

        });

        binding.addSource.setOnClickListener(v -> {
            if (intent.getStringExtra("Type").contains("ExistNetwork")) {
                if (CableKml != null) {
                    BoundingBox boundingBox = CableKml.mKmlRoot.getBoundingBox();
                    binding.map.zoomToBoundingBox(boundingBox, true);
                    binding.map.getBoundingBox().getCenter();
                    binding.map.getController().setZoom(7.0);
                    binding.map.getController().setCenter(boundingBox.getCenter());
                    binding.map.zoomToBoundingBox(boundingBox.increaseByScale(1.3f), true);
                    binding.map.invalidate();
                } else if (OverHeadKml != null) {
                    BoundingBox boundingBox = OverHeadKml.mKmlRoot.getBoundingBox();
                    binding.map.zoomToBoundingBox(boundingBox, true);
                    binding.map.getBoundingBox().getCenter();
                    binding.map.getController().setZoom(7.0);
                    binding.map.getController().setCenter(boundingBox.getCenter());
                    binding.map.zoomToBoundingBox(boundingBox.increaseByScale(1.3f), true);
                    binding.map.invalidate();
                } else {
                    binding.map.getController().animateTo(sourcePoint, 25.0, 0L);
                    binding.map.getController().setCenter(sourcePoint);
                    binding.map.invalidate();
                }
            } else {
                if (ResponseDataUtils.isLocationEnabled(MapActivity.this)) {
                    if (locationOver != null) {
                        checkPermissions();
                    }
                } else {
                    showEnabledLocationDialog();
                }

            }
        });

        binding.drawOption.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, v);
            popupMenu.getMenuInflater().inflate(R.menu.draw_option_menu, popupMenu.getMenu());
            try {
                Field popup = PopupMenu.class.getDeclaredField("mPopup");
                popup.setAccessible(true);
                Object menuPopupHelper = popup.get(popupMenu);
                assert menuPopupHelper != null;
                Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                Method setForceShowIcon = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                setForceShowIcon.invoke(menuPopupHelper, true);
                popupMenu.getMenu().findItem(R.id.draw_current_location).setChecked(isDrawCurrentLocationChecked);
                popupMenu.getMenu().findItem(R.id.draw_menualy).setChecked(isDrawManuallyChecked);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.draw_current_location:
                                isDrawCurrentLocationChecked = !isDrawCurrentLocationChecked;
                                isDrawManuallyChecked = false;
                                menuItem.setChecked(isDrawCurrentLocationChecked);
                                popupMenu.getMenu().findItem(R.id.draw_menualy).setChecked(false);
                                if (isDrawCurrentLocationChecked) {
                                    binding.addNewNode.setVisibility(View.VISIBLE);
                                    binding.addSource.setVisibility(View.VISIBLE);
                                    binding.map.getOverlays().remove(evOverlay);
                                } else {
                                    binding.addNewNode.setVisibility(View.GONE);
                                    binding.addSource.setVisibility(View.GONE);
                                    binding.map.getOverlays().add(evOverlay);
                                }
                                binding.map.invalidate();
                                return true;

                            case R.id.draw_menualy:
                                isDrawManuallyChecked = !isDrawManuallyChecked;
                                isDrawCurrentLocationChecked = false;
                                menuItem.setChecked(isDrawManuallyChecked);
                                popupMenu.getMenu().findItem(R.id.draw_current_location).setChecked(false);
                                if (isDrawManuallyChecked) {
                                    binding.addNewNode.setVisibility(View.GONE);
                                    binding.addSource.setVisibility(View.GONE);
                                    binding.map.getOverlays().add(evOverlay);
                                } else {
                                    binding.addNewNode.setVisibility(View.VISIBLE);
                                    binding.addSource.setVisibility(View.VISIBLE);
                                    binding.map.getOverlays().remove(evOverlay);
                                }
                                binding.map.invalidate();
                                return true;

                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            } catch (Exception e) {
                Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
            }
        });

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onRestart() {
        super.onRestart();
        if (!checkPermission()) {
            Snackbar.make(binding.getRoot(), "Please Grand The Permission", Snackbar.LENGTH_INDEFINITE).show();
            return;
        } else {
            if (locationOverlay != null) {
                locationOverlay.enableMyLocation();
                locationOverlay.setEnabled(true);
            }
        }

        if (binding.map.getOverlays().isEmpty()) {
            if (Objects.requireNonNull(intent.getStringExtra("Type")).equals("AddNetwork")) {
                if (!ResponseDataUtils.isLocationEnabled(this)) {
                    showEnabledLocationDialog();
                    return;
                }
                binding.sideLLayout.setVisibility(View.GONE);
                binding.colorCodeView.setVisibility(View.GONE);
                binding.menuLayout.setVisibility(View.GONE);
                //Previous visible
                binding.surveyMenuLayout.setVisibility(View.VISIBLE);
                binding.surveyLLayout.setVisibility(View.VISIBLE);
                binding.locateLocation.setVisibility(View.VISIBLE);
                binding.addSource.setVisibility(View.VISIBLE);
            } else if (Objects.requireNonNull(intent.getStringExtra("Type")).equals("ExistNetwork")) {
                if (!ResponseDataUtils.isLocationEnabled(this)) {
                    showEnabledLocationDialog();
                    return;
                }
                binding.sideLLayout.setVisibility(View.GONE);
                binding.colorCodeView.setVisibility(View.GONE);
                binding.menuLayout.setVisibility(View.GONE);
                //Previous visible
                binding.addSource.setVisibility(View.GONE);
                binding.surveyMenuLayout.setVisibility(View.GONE);
                binding.surveyLLayout.setVisibility(View.GONE);
                binding.locateLocation.setVisibility(View.GONE);
                if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(MapActivity.this)) {
                    if (intent.getStringArrayListExtra("NetworkId") != null) {
                        getSurveyNetworkData(intent.getStringArrayListExtra("NetworkId").get(0).trim());
                    }
                } else {
                    Snackbar.make(binding.getRoot(), R.string.no_internet_connection, Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(MapActivity.this)) {
                                if (intent.getStringArrayListExtra("NetworkId").get(0) != null) {
                                    getSurveyNetworkData(intent.getStringArrayListExtra("NetworkId").get(0).trim());
                                }
                            }
                        }
                    }).show();
                }
            } else {
                binding.sideLLayout.setVisibility(View.GONE);
                binding.colorCodeView.setVisibility(View.GONE);
                binding.menuLayout.setVisibility(View.GONE);

                binding.surveyMenuLayout.setVisibility(View.GONE);
                binding.surveyLLayout.setVisibility(View.GONE);
                binding.locateLocation.setVisibility(View.GONE);

                if (prefManager.getUserType().contains("View")) {
                    binding.analysisImgBtn.setVisibility(View.GONE);
                    binding.deleteBtn.setVisibility(View.GONE);
                    binding.newConnectionLoadFlowStatusImgBtn.setVisibility(View.GONE);
                } else if (prefManager.getUserType().contains("Edit")) {
                    binding.analysisImgBtn.setVisibility(View.VISIBLE);
                    binding.deleteBtn.setVisibility(View.VISIBLE);
                    binding.newConnectionLoadFlowStatusImgBtn.setVisibility(View.VISIBLE);
                } else if (prefManager.getUserType().contains("Analysis")) {
                    binding.analysisImgBtn.setVisibility(View.VISIBLE);
                    binding.deleteBtn.setVisibility(View.GONE);
                    binding.newConnectionLoadFlowStatusImgBtn.setVisibility(View.GONE);
                } else {
                    binding.deleteBtn.setVisibility(View.GONE);
                    binding.newConnectionLoadFlowStatusImgBtn.setVisibility(View.GONE);
                    binding.analysisImgBtn.setVisibility(View.VISIBLE);
                }
                if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(MapActivity.this)) {
                    JsonArray jsonArray = new Gson().toJsonTree(intent.getStringArrayListExtra("NetworkId")).getAsJsonArray();
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<String>>() {
                    }.getType();
                    selectedFeeder = gson.fromJson(jsonArray, listType);
                    if (selectedFeeder != null && !selectedFeeder.isEmpty() && selectedFeeder.get(0) != null) {
                        getNetworkData(selectedFeeder.get(0).trim());
                    }
                } else {
                    Snackbar.make(binding.getRoot(), R.string.no_internet_connection, Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(MapActivity.this)) {
                                if (selectedFeeder != null && !selectedFeeder.isEmpty() && selectedFeeder.get(0) != null) {
                                    getNetworkData(intent.getStringArrayListExtra("NetworkId").get(0).trim());
                                }
                            }
                        }
                    }).show();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!checkPermission()) {
            Snackbar.make(binding.getRoot(), "Please Grand The Permission", Snackbar.LENGTH_INDEFINITE).show();
        } else {
            if (locationOverlay != null) {
                locationOverlay.enableMyLocation();
                locationOverlay.setEnabled(true);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (locationOverlay != null) {
            locationOverlay.disableMyLocation();
            locationOverlay.setEnabled(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressBarLayout != null) {
            progressBarLayout.stopAnimation();
        }
        if (locationOverlay != null) {
            locationOverlay.disableMyLocation();
            locationOverlay.setEnabled(false);
        }
    }

    @Override
    public boolean onClose() {
        adapter.filterData("");
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        adapter.filterData(query);
        expandAll();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if (adapter != null) {
            adapter.filterData(query);
            expandAll();
        }
        return false;
    }

    @Override
    public void onJsonObjectReceived(JsonObject jsonObject, JsonObject dashBoardJsonObject, List<String> list) {
        try {
            loadFlow.dismiss();
            JSONObject jsonObject2 = new JSONObject(jsonObject.toString());
            if (jsonObject2.has("isLoadFlow")) {
                progressBarLayout.setProcessText("Load-Flow Analysis Run...");
                progressBarLayout.setVisibility(View.VISIBLE);
                Config.isLoadFlow = jsonObject2.getBoolean("isLoadFlow");
                Config.isShortCircuit = jsonObject2.getBoolean("isLoadFlow");
                binding.reportBtn.setVisibility(View.GONE);
                CancelAnalysis();
            } else {
                LoadFlowAnalysis(jsonObject);
                loadFlowList = list;
            }
        } catch (Exception e) {
            Log.d("Main", Objects.requireNonNull(e.getLocalizedMessage()));
        }
    }

    @Override
    public void onShortCircuitArgReceived(JsonObject jsonObject, List<String> list) {
        if (jsonObject.has("ShortCircuit")) {
            shortCircuit.dismiss();
            Config.isShortCircuit = false;
            Config.isLoadFlow = false;
            binding.reportBtn.setVisibility(View.GONE);
            CancelAnalysis();
        } else {
            shortCircuit.dismiss();
            shortCircuitList = list;
            ShortCircuitAnalysis(jsonObject);
        }
    }

    @Override
    public void onLoadAllocationCallBack(JsonObject jsonObject) {
        if (jsonObject.has("LoadAllocation")) {
            binding.reportBtn.setVisibility(View.GONE);
        } else {
            LoadAllocationAnalysis(jsonObject);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHILD_ACTIVITY && resultCode == RESULT_OK) {
            if (data != null) {
                if (data.getStringExtra("DeviceNo") != null && !Objects.requireNonNull(data.getStringExtra("DeviceNo")).isEmpty() && data.getStringExtra("DeviceType") != null && !Objects.requireNonNull(data.getStringExtra("DeviceType")).isEmpty() && prefManager.getUserType() != null) {
                    getDevices(data.getStringExtra("DeviceNo"), Objects.requireNonNull(data.getStringExtra("DeviceType")));
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MULTIPLE_PERMISSIONS) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                PrefManager prefManager = new PrefManager(MapActivity.this);
                prefManager.setIsFirstTimeUser(true);
                startActivity(new Intent(MapActivity.this, SplashScreen.class));
                finish();
            } else {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, 100);
            }
        } else if (requestCode == 100) {
            checkPermissions();
        }
        /*  if (requestCode == MULTIPLE_PERMISSIONS) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                PrefManager prefManager = new PrefManager(MapActivity.this);
                prefManager.setIsFirstTimeUser(true);
                startActivity(new Intent(MapActivity.this, SplashScreen.class));
                finish();
            } else {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, 100);
            }
        } else if (requestCode == 100) {
            checkPermissions();
        }
*/
    }

    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        networkIdList.clear();
        if (Config.isLoadFlow) {
            Config.isLoadFlow = false;
        }
        if (Config.isShortCircuit) {
            Config.isShortCircuit = false;
        }
        super.onBackPressed();
    }

    @Override
    public void addDevice(String sectionType, JsonObject jsonObject) {
        if (jsonObject.has("isCancel")) {
            Args.setSectionValidate(false);
            Args.setISBreakerValidate(false);
            Args.setIsCapacitorValidate(false);
            Args.setIsFuseValidate(false);
            Args.setIsSwitchValidate(false);
            Args.setIsTransformerValidate(false);
            clearVertexNode();
        } else {
            Args.setSectionValidate(false);
            Args.setISBreakerValidate(false);
            Args.setIsCapacitorValidate(false);
            Args.setIsFuseValidate(false);
            Args.setIsSwitchValidate(false);
            Args.setIsTransformerValidate(false);
            addNewSectionDevices(sectionType, jsonObject);
        }
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        return false;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        if (Objects.requireNonNull(intent.getStringExtra("Type")).contains("AddNetwork")) {
            if (!isSource) {
                SourceEditDialog sourceEditDialog = new SourceEditDialog(this, getSupportFragmentManager(), getLifecycle(), p.getLatitude(), p.getLongitude());
                sourceEditDialog.setCancelable(false);
                sourceEditDialog.show();
            } else if (!coordinateList.isEmpty() && !newSectionGeoPointList.isEmpty() && selectedNodeID != null) {
                coordinateList.add(p);
                newSectionGeoPointList.add(p);
                addNewSections(coordinateList);
            } else {
                Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Please select From Node!", Snackbar.LENGTH_LONG);
                snack.show();
            }
        } else if (Objects.requireNonNull(intent.getStringExtra("Type")).contains("ExistNetwork")) {
            if (!newSectionGeoPointList.isEmpty() && !coordinateList.isEmpty() && selectedNodeID != null) {
                newSectionGeoPointList.add(p);
                coordinateList.add(p);
                addNewSections(coordinateList);
            } else {
                Snackbar.make(binding.getRoot(), "Please Select From Node", Snackbar.LENGTH_LONG).show();
            }
        }
        return true;
    }

    private void DeleteSection(String delSectionId, String delDeviceNumber, String delDeviceType, String delNetworkId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("SectionId", delSectionId);
        jsonObject.addProperty("Username", prefManager.getUserName());
        jsonObject.addProperty("DeviceNumber", delDeviceNumber);
        jsonObject.addProperty("DeviceType", Integer.valueOf(delDeviceType));
        jsonObject.addProperty("NodeId", "");
        jsonObject.addProperty("NetworkId", delNetworkId);
        jsonObject.addProperty("CYMDBNET", prefManager.getDBName());
        Retrofit retrofit = RetrofitClient.getClient(this);
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<DeleteFeature> call = apiInterface.DeleteFeatures(jsonObject);
        call.enqueue(new Callback<DeleteFeature>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<DeleteFeature> call, @NonNull Response<DeleteFeature> response) {
                if (response.code() == 200) {
                    try {
                        DeleteFeature deleteFeature = response.body();
                        assert deleteFeature != null;
                        if (DelDeviceType.equals("1") || DelDeviceType.equals("2") || DelDeviceType.equals("23")) {
                            DelSectionId = null;
                            DelDeviceNumber = null;
                            DelDeviceType = null;
                            DelNetworkId = null;
                            if (deleteFeature.getMessage().equalsIgnoreCase("Feature Deleted Successfully")) {
                                if (findDeviceModel.getOutput() != null && !findDeviceModel.getOutput().isEmpty()) {
                                    for (int i = 0; i < findDeviceModel.getOutput().size(); i++) {
                                        if (findDeviceModel.getOutput().get(i).getDeviceType() == 1) {
                                            if (CableFolderOverLay.getItems().contains(CaSectionId.get(findDeviceModel.getOutput().get(i).getDeviceNumber()))) {
                                                CableFolderOverLay.getItems().remove(CaSectionId.get(findDeviceModel.getOutput().get(i).getDeviceNumber()));
                                                binding.map.invalidate();

                                                for (int j = 0; j < continentList.size(); j++) {
                                                    if (continentList.get(j).getName().contains("Cable")) {
                                                        List<DType> deviceList = continentList.get(j).getDeviceList();
                                                        String targetDeviceNumber = findDeviceModel.getOutput().get(i).getDeviceNumber();

                                                        Iterator<DType> iterator = deviceList.iterator();
                                                        while (iterator.hasNext()) {
                                                            DType device = iterator.next();
                                                            if (device.getName().equals(targetDeviceNumber)) {
                                                                iterator.remove();
                                                                break;
                                                            }
                                                        }

                                                        continentList.get(j).setName("Cable (" + deviceList.size() + ")");
                                                        adapter = new ExpandableDeviceAdapter(MapActivity.this, continentList);
                                                        binding.navigationmenu.setAdapter(adapter);
                                                    }
                                                }

                                            }
                                            if (nodeOverLay.getItems().contains(NodeId.get(findDeviceModel.getOutput().get(i).getToNodeId()))) {
                                                nodeOverLay.getItems().remove(NodeId.get(findDeviceModel.getOutput().get(i).getToNodeId()));
                                                binding.map.invalidate();
                                            }
                                        } else if (findDeviceModel.getOutput().get(i).getDeviceType() == 2) {
                                            if (OverheadFolderOverLay.getItems().contains(OhSectionId.get(findDeviceModel.getOutput().get(i).getDeviceNumber()))) {
                                                OverheadFolderOverLay.getItems().remove(OhSectionId.get(findDeviceModel.getOutput().get(i).getDeviceNumber()));
                                                binding.map.invalidate();

                                                for (int j = 0; j < continentList.size(); j++) {
                                                    if (continentList.get(j).getName().contains("Overhead Balance")) {
                                                        List<DType> deviceList = continentList.get(j).getDeviceList();
                                                        String targetDeviceNumber = findDeviceModel.getOutput().get(i).getDeviceNumber();

                                                        Iterator<DType> iterator = deviceList.iterator();
                                                        while (iterator.hasNext()) {
                                                            DType device = iterator.next();
                                                            if (device.getName().equals(targetDeviceNumber)) {
                                                                iterator.remove();
                                                                break;
                                                            }
                                                        }

                                                        continentList.get(j).setName("Overhead Balance (" + deviceList.size() + ")");
                                                        adapter = new ExpandableDeviceAdapter(MapActivity.this, continentList);
                                                        binding.navigationmenu.setAdapter(adapter);
                                                    }
                                                }

                                            }
                                            if (nodeOverLay.getItems().contains(NodeId.get(findDeviceModel.getOutput().get(i).getToNodeId()))) {
                                                nodeOverLay.getItems().remove(NodeId.get(findDeviceModel.getOutput().get(i).getToNodeId()));
                                                binding.map.invalidate();
                                            }
                                        } else if (findDeviceModel.getOutput().get(i).getDeviceType() == 23) {
                                            if (UnBalanceFolderOverLay.getItems().contains(UnBalSectionId.get(findDeviceModel.getOutput().get(i).getDeviceNumber()))) {
                                                UnBalanceFolderOverLay.getItems().remove(UnBalSectionId.get(findDeviceModel.getOutput().get(i).getDeviceNumber()));
                                                binding.map.invalidate();

                                                for (int j = 0; j < continentList.size(); j++) {
                                                    if (continentList.get(j).getName().contains("Unbalance")) {
                                                        List<DType> deviceList = continentList.get(j).getDeviceList();
                                                        String targetDeviceNumber = findDeviceModel.getOutput().get(i).getDeviceNumber();

                                                        Iterator<DType> iterator = deviceList.iterator();
                                                        while (iterator.hasNext()) {
                                                            DType device = iterator.next();
                                                            if (device.getName().equals(targetDeviceNumber)) {
                                                                iterator.remove();
                                                                break;
                                                            }
                                                        }

                                                        continentList.get(j).setName("Unbalance (" + deviceList.size() + ")");
                                                        adapter = new ExpandableDeviceAdapter(MapActivity.this, continentList);
                                                        binding.navigationmenu.setAdapter(adapter);
                                                    }
                                                }

                                            }

                                            if (nodeOverLay.getItems().contains(NodeId.get(findDeviceModel.getOutput().get(i).getToNodeId()))) {
                                                nodeOverLay.getItems().remove(NodeId.get(findDeviceModel.getOutput().get(i).getToNodeId()));
                                                binding.map.invalidate();
                                            }
                                        } else if (findDeviceModel.getOutput().get(i).getDeviceType() == 8) {
                                            if (CircuitBreakerOverLay.getItems().contains(breakerSectionId.get(findDeviceModel.getOutput().get(i).getDeviceNumber()))) {
                                                CircuitBreakerOverLay.getItems().remove(breakerSectionId.get(findDeviceModel.getOutput().get(i).getDeviceNumber()));
                                                binding.map.invalidate();

                                                for (int j = 0; j < continentList.size(); j++) {
                                                    if (continentList.get(j).getName().contains("Breaker")) {
                                                        List<DType> deviceList = continentList.get(j).getDeviceList();
                                                        String targetDeviceNumber = findDeviceModel.getOutput().get(i).getDeviceNumber();

                                                        Iterator<DType> iterator = deviceList.iterator();
                                                        while (iterator.hasNext()) {
                                                            DType device = iterator.next();
                                                            if (device.getName().equals(targetDeviceNumber)) {
                                                                iterator.remove();
                                                                break;
                                                            }
                                                        }

                                                        continentList.get(j).setName("Breaker (" + deviceList.size() + ")");
                                                        adapter = new ExpandableDeviceAdapter(MapActivity.this, continentList);
                                                        binding.navigationmenu.setAdapter(adapter);
                                                    }
                                                }
                                            }
                                        } else if (findDeviceModel.getOutput().get(i).getDeviceType() == 17) {
                                            if (ShuntCapacitorOverLay.getItems().contains(capacitorSectionId.get(findDeviceModel.getOutput().get(i).getDeviceNumber()))) {
                                                ShuntCapacitorOverLay.getItems().remove(capacitorSectionId.get(findDeviceModel.getOutput().get(i).getDeviceNumber()));
                                                binding.map.invalidate();

                                                for (int j = 0; j < continentList.size(); j++) {
                                                    if (continentList.get(j).getName().contains("Shunt Capacitor")) {
                                                        List<DType> deviceList = continentList.get(j).getDeviceList();
                                                        String targetDeviceNumber = findDeviceModel.getOutput().get(i).getDeviceNumber();

                                                        Iterator<DType> iterator = deviceList.iterator();
                                                        while (iterator.hasNext()) {
                                                            DType device = iterator.next();
                                                            if (device.getName().equals(targetDeviceNumber)) {
                                                                iterator.remove();
                                                                break;
                                                            }
                                                        }

                                                        continentList.get(j).setName("Shunt Capacitor (" + deviceList.size() + ")");
                                                        adapter = new ExpandableDeviceAdapter(MapActivity.this, continentList);
                                                        binding.navigationmenu.setAdapter(adapter);
                                                    }
                                                }
                                            }
                                        } else if (findDeviceModel.getOutput().get(i).getDeviceType() == 14) {
                                            if (FuseOverLay.getItems().contains(fuseSectionId.get(findDeviceModel.getOutput().get(i).getDeviceNumber()))) {
                                                FuseOverLay.getItems().remove(fuseSectionId.get(findDeviceModel.getOutput().get(i).getDeviceNumber()));
                                                binding.map.invalidate();

                                                for (int j = 0; j < continentList.size(); j++) {
                                                    if (continentList.get(j).getName().contains("Fuse")) {
                                                        List<DType> deviceList = continentList.get(j).getDeviceList();
                                                        String targetDeviceNumber = findDeviceModel.getOutput().get(i).getDeviceNumber();

                                                        Iterator<DType> iterator = deviceList.iterator();
                                                        while (iterator.hasNext()) {
                                                            DType device = iterator.next();
                                                            if (device.getName().equals(targetDeviceNumber)) {
                                                                iterator.remove();
                                                                break;
                                                            }
                                                        }

                                                        continentList.get(j).setName("Fuse (" + deviceList.size() + ")");
                                                        adapter = new ExpandableDeviceAdapter(MapActivity.this, continentList);
                                                        binding.navigationmenu.setAdapter(adapter);
                                                    }
                                                }
                                            }
                                        } else if (findDeviceModel.getOutput().get(i).getDeviceType() == 20) {
                                            if (SpotLoadOverLay.getItems().contains(spotLoadSectionId.get(findDeviceModel.getOutput().get(i).getDeviceNumber()))) {
                                                SpotLoadOverLay.getItems().remove(spotLoadSectionId.get(findDeviceModel.getOutput().get(i).getDeviceNumber()));
                                                binding.map.invalidate();

                                                for (int j = 0; j < continentList.size(); j++) {
                                                    if (continentList.get(j).getName().contains("SpotLoad")) {
                                                        List<DType> deviceList = continentList.get(j).getDeviceList();
                                                        String targetDeviceNumber = findDeviceModel.getOutput().get(i).getDeviceNumber();

                                                        Iterator<DType> iterator = deviceList.iterator();
                                                        while (iterator.hasNext()) {
                                                            DType device = iterator.next();
                                                            if (device.getName().equals(targetDeviceNumber)) {
                                                                iterator.remove();
                                                                break;
                                                            }
                                                        }

                                                        continentList.get(j).setName("SpotLoad (" + deviceList.size() + ")");
                                                        adapter = new ExpandableDeviceAdapter(MapActivity.this, continentList);
                                                        binding.navigationmenu.setAdapter(adapter);
                                                    }
                                                }
                                            }
                                        } else if (findDeviceModel.getOutput().get(i).getDeviceType() == 13) {
                                            if (SwitchOverLay.getItems().contains(switchSectionId.get(findDeviceModel.getOutput().get(i).getDeviceNumber()))) {
                                                SwitchOverLay.getItems().remove(switchSectionId.get(findDeviceModel.getOutput().get(i).getDeviceNumber()));
                                                binding.map.invalidate();

                                                for (int j = 0; j < continentList.size(); j++) {
                                                    if (continentList.get(j).getName().contains("Switch")) {
                                                        List<DType> deviceList = continentList.get(j).getDeviceList();
                                                        String targetDeviceNumber = findDeviceModel.getOutput().get(i).getDeviceNumber();

                                                        Iterator<DType> iterator = deviceList.iterator();
                                                        while (iterator.hasNext()) {
                                                            DType device = iterator.next();
                                                            if (device.getName().equals(targetDeviceNumber)) {
                                                                iterator.remove();
                                                                break;
                                                            }
                                                        }

                                                        continentList.get(j).setName("Switch (" + deviceList.size() + ")");
                                                        adapter = new ExpandableDeviceAdapter(MapActivity.this, continentList);
                                                        binding.navigationmenu.setAdapter(adapter);
                                                    }
                                                }
                                            }
                                        } else if (findDeviceModel.getOutput().get(i).getDeviceType() == 5) {
                                            if (DistributionTransferOverLay.getItems().contains(transformerSectionId.get(findDeviceModel.getOutput().get(i).getDeviceNumber()))) {
                                                DistributionTransferOverLay.getItems().remove(transformerSectionId.get(findDeviceModel.getOutput().get(i).getDeviceNumber()));
                                                binding.map.invalidate();

                                                for (int j = 0; j < continentList.size(); j++) {
                                                    if (continentList.get(j).getName().contains("Two-Winding Transformer")) {
                                                        List<DType> deviceList = continentList.get(j).getDeviceList();
                                                        String targetDeviceNumber = findDeviceModel.getOutput().get(i).getDeviceNumber();

                                                        Iterator<DType> iterator = deviceList.iterator();
                                                        while (iterator.hasNext()) {
                                                            DType device = iterator.next();
                                                            if (device.getName().equals(targetDeviceNumber)) {
                                                                iterator.remove();
                                                                break;
                                                            }
                                                        }

                                                        continentList.get(j).setName("Two-Winding Transformer (" + deviceList.size() + ")");
                                                        adapter = new ExpandableDeviceAdapter(MapActivity.this, continentList);
                                                        binding.navigationmenu.setAdapter(adapter);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            DelSectionId = null;
                            DelDeviceNumber = null;
                            DelDeviceType = null;
                            DelNetworkId = null;
                            if (deleteFeature.getMessage().contains("Feature Deleted Successfully")) {
                                if (findDeviceModel.getOutput() != null && !findDeviceModel.getOutput().isEmpty()) {
                                    for (int i = 0; i < findDeviceModel.getOutput().size(); i++) {
                                        if (findDeviceModel.getOutput().get(i).getDeviceType() == 8) {
                                            if (CircuitBreakerOverLay.getItems().contains(breakerSectionId.get(findDeviceModel.getOutput().get(i).getDeviceNumber()))) {
                                                CircuitBreakerOverLay.getItems().remove(breakerSectionId.get(findDeviceModel.getOutput().get(i).getDeviceNumber()));

                                                if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                                    if (secNodeSectionId.get(findDeviceModel.getOutput().get(i).getSectionId()) != null) {
                                                        sectionFolderOverLay.getItems().remove(secNodeSectionId.get(findDeviceModel.getOutput().get(i).getSectionId()));
                                                    }
                                                }

                                                List<Integer> deviceTypeList = new ArrayList<>();
                                                for (int j = 0; j < findDeviceModel.getOutput().size(); j++) {
                                                    deviceTypeList.add(findDeviceModel.getOutput().get(j).getDeviceType());
                                                }

                                                if (!deviceTypeList.contains(1) || !deviceTypeList.contains(2) || !deviceTypeList.contains(23)) {
                                                    if (nodeOverLay.getItems().contains(NodeId.get(findDeviceModel.getOutput().get(i).getToNodeId()))) {
                                                        nodeOverLay.getItems().remove(NodeId.get(findDeviceModel.getOutput().get(i).getToNodeId()));
                                                    }
                                                }

                                                for (int j = 0; j < continentList.size(); j++) {
                                                    if (continentList.get(j).getName().contains("Breaker")) {
                                                        List<DType> deviceList = continentList.get(j).getDeviceList();
                                                        String targetDeviceNumber = findDeviceModel.getOutput().get(i).getDeviceNumber();

                                                        Iterator<DType> iterator = deviceList.iterator();
                                                        while (iterator.hasNext()) {
                                                            DType device = iterator.next();
                                                            if (device.getName().equals(targetDeviceNumber)) {
                                                                iterator.remove();
                                                                break;
                                                            }
                                                        }

                                                        continentList.get(j).setName("Breaker (" + deviceList.size() + ")");
                                                        adapter = new ExpandableDeviceAdapter(MapActivity.this, continentList);
                                                        binding.navigationmenu.setAdapter(adapter);
                                                    }
                                                }
                                            }

                                        } else if (findDeviceModel.getOutput().get(i).getDeviceType() == 17) {
                                            if (ShuntCapacitorOverLay.getItems().contains(capacitorSectionId.get(findDeviceModel.getOutput().get(i).getDeviceNumber()))) {
                                                ShuntCapacitorOverLay.getItems().remove(capacitorSectionId.get(findDeviceModel.getOutput().get(i).getDeviceNumber()));

                                                if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                                    if (secNodeSectionId.get(findDeviceModel.getOutput().get(i).getSectionId()) != null) {
                                                        sectionFolderOverLay.getItems().remove(secNodeSectionId.get(findDeviceModel.getOutput().get(i).getSectionId()));
                                                    }
                                                }

                                                List<Integer> deviceTypeList = new ArrayList<>();
                                                for (int j = 0; j < findDeviceModel.getOutput().size(); j++) {
                                                    deviceTypeList.add(findDeviceModel.getOutput().get(j).getDeviceType());
                                                }

                                                if (!deviceTypeList.contains(1) || !deviceTypeList.contains(2) || !deviceTypeList.contains(23)) {
                                                    if (nodeOverLay.getItems().contains(NodeId.get(findDeviceModel.getOutput().get(i).getToNodeId()))) {
                                                        nodeOverLay.getItems().remove(NodeId.get(findDeviceModel.getOutput().get(i).getToNodeId()));
                                                    }
                                                }
                                                binding.map.invalidate();

                                                for (int j = 0; j < continentList.size(); j++) {
                                                    if (continentList.get(j).getName().contains("Shunt Capacitor")) {
                                                        List<DType> deviceList = continentList.get(j).getDeviceList();
                                                        String targetDeviceNumber = findDeviceModel.getOutput().get(i).getDeviceNumber();

                                                        Iterator<DType> iterator = deviceList.iterator();
                                                        while (iterator.hasNext()) {
                                                            DType device = iterator.next();
                                                            if (device.getName().equals(targetDeviceNumber)) {
                                                                iterator.remove();
                                                                break;
                                                            }
                                                        }

                                                        continentList.get(j).setName("Shunt Capacitor (" + deviceList.size() + ")");
                                                        adapter = new ExpandableDeviceAdapter(MapActivity.this, continentList);
                                                        binding.navigationmenu.setAdapter(adapter);
                                                    }
                                                }

                                            }
                                        } else if (findDeviceModel.getOutput().get(i).getDeviceType() == 14) {
                                            if (FuseOverLay.getItems().contains(fuseSectionId.get(findDeviceModel.getOutput().get(i).getDeviceNumber()))) {
                                                FuseOverLay.getItems().remove(fuseSectionId.get(findDeviceModel.getOutput().get(i).getDeviceNumber()));

                                                if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                                    if (secNodeSectionId.get(findDeviceModel.getOutput().get(i).getSectionId()) != null) {
                                                        sectionFolderOverLay.getItems().remove(secNodeSectionId.get(findDeviceModel.getOutput().get(i).getSectionId()));
                                                    }
                                                }

                                                List<Integer> deviceTypeList = new ArrayList<>();
                                                for (int j = 0; j < findDeviceModel.getOutput().size(); j++) {
                                                    deviceTypeList.add(findDeviceModel.getOutput().get(j).getDeviceType());
                                                }

                                                if (!deviceTypeList.contains(1) || !deviceTypeList.contains(2) || !deviceTypeList.contains(23)) {
                                                    if (nodeOverLay.getItems().contains(NodeId.get(findDeviceModel.getOutput().get(i).getToNodeId()))) {
                                                        nodeOverLay.getItems().remove(NodeId.get(findDeviceModel.getOutput().get(i).getToNodeId()));
                                                    }
                                                }
                                                binding.map.invalidate();

                                                for (int j = 0; j < continentList.size(); j++) {
                                                    if (continentList.get(j).getName().contains("Fuse")) {
                                                        List<DType> deviceList = continentList.get(j).getDeviceList();
                                                        String targetDeviceNumber = findDeviceModel.getOutput().get(i).getDeviceNumber();

                                                        Iterator<DType> iterator = deviceList.iterator();
                                                        while (iterator.hasNext()) {
                                                            DType device = iterator.next();
                                                            if (device.getName().equals(targetDeviceNumber)) {
                                                                iterator.remove();
                                                                break;
                                                            }
                                                        }

                                                        continentList.get(j).setName("Fuse (" + deviceList.size() + ")");
                                                        adapter = new ExpandableDeviceAdapter(MapActivity.this, continentList);
                                                        binding.navigationmenu.setAdapter(adapter);
                                                    }
                                                }

                                            }
                                        } else if (findDeviceModel.getOutput().get(i).getDeviceType() == 20) {
                                            if (SpotLoadOverLay.getItems().contains(spotLoadSectionId.get(findDeviceModel.getOutput().get(i).getDeviceNumber()))) {
                                                SpotLoadOverLay.getItems().remove(spotLoadSectionId.get(findDeviceModel.getOutput().get(i).getDeviceNumber()));

                                                if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                                    if (secNodeSectionId.get(findDeviceModel.getOutput().get(i).getSectionId()) != null) {
                                                        sectionFolderOverLay.getItems().remove(secNodeSectionId.get(findDeviceModel.getOutput().get(i).getSectionId()));
                                                    }
                                                }

                                                List<Integer> deviceTypeList = new ArrayList<>();
                                                for (int j = 0; j < findDeviceModel.getOutput().size(); j++) {
                                                    deviceTypeList.add(findDeviceModel.getOutput().get(j).getDeviceType());
                                                }

                                                if (!deviceTypeList.contains(1) || !deviceTypeList.contains(2) || !deviceTypeList.contains(23)) {
                                                    if (nodeOverLay.getItems().contains(NodeId.get(findDeviceModel.getOutput().get(i).getToNodeId()))) {
                                                        nodeOverLay.getItems().remove(NodeId.get(findDeviceModel.getOutput().get(i).getToNodeId()));
                                                    }
                                                }
                                                binding.map.invalidate();

                                                for (int j = 0; j < continentList.size(); j++) {
                                                    if (continentList.get(j).getName().contains("SpotLoad")) {
                                                        List<DType> deviceList = continentList.get(j).getDeviceList();
                                                        String targetDeviceNumber = findDeviceModel.getOutput().get(i).getDeviceNumber();

                                                        Iterator<DType> iterator = deviceList.iterator();
                                                        while (iterator.hasNext()) {
                                                            DType device = iterator.next();
                                                            if (device.getName().equals(targetDeviceNumber)) {
                                                                iterator.remove();
                                                                break;
                                                            }
                                                        }

                                                        continentList.get(j).setName("SpotLoad (" + deviceList.size() + ")");
                                                        adapter = new ExpandableDeviceAdapter(MapActivity.this, continentList);
                                                        binding.navigationmenu.setAdapter(adapter);
                                                    }
                                                }

                                            }
                                        } else if (findDeviceModel.getOutput().get(i).getDeviceType() == 13) {
                                            if (SwitchOverLay.getItems().contains(switchSectionId.get(findDeviceModel.getOutput().get(i).getDeviceNumber()))) {
                                                SwitchOverLay.getItems().remove(switchSectionId.get(findDeviceModel.getOutput().get(i).getDeviceNumber()));

                                                if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                                    if (secNodeSectionId.get(findDeviceModel.getOutput().get(i).getSectionId()) != null) {
                                                        sectionFolderOverLay.getItems().remove(secNodeSectionId.get(findDeviceModel.getOutput().get(i).getSectionId()));
                                                    }
                                                }

                                                List<Integer> deviceTypeList = new ArrayList<>();
                                                for (int j = 0; j < findDeviceModel.getOutput().size(); j++) {
                                                    deviceTypeList.add(findDeviceModel.getOutput().get(j).getDeviceType());
                                                }

                                                if (!deviceTypeList.contains(1) || !deviceTypeList.contains(2) || !deviceTypeList.contains(23)) {
                                                    if (nodeOverLay.getItems().contains(NodeId.get(findDeviceModel.getOutput().get(i).getToNodeId()))) {
                                                        nodeOverLay.getItems().remove(NodeId.get(findDeviceModel.getOutput().get(i).getToNodeId()));
                                                    }
                                                }
                                                binding.map.invalidate();

                                                for (int j = 0; j < continentList.size(); j++) {
                                                    if (continentList.get(j).getName().contains("Switch")) {
                                                        List<DType> deviceList = continentList.get(j).getDeviceList();
                                                        String targetDeviceNumber = findDeviceModel.getOutput().get(i).getDeviceNumber();

                                                        Iterator<DType> iterator = deviceList.iterator();
                                                        while (iterator.hasNext()) {
                                                            DType device = iterator.next();
                                                            if (device.getName().equals(targetDeviceNumber)) {
                                                                iterator.remove();
                                                                break;
                                                            }
                                                        }

                                                        continentList.get(j).setName("Switch (" + deviceList.size() + ")");
                                                        adapter = new ExpandableDeviceAdapter(MapActivity.this, continentList);
                                                        binding.navigationmenu.setAdapter(adapter);
                                                    }
                                                }

                                            }
                                        } else if (findDeviceModel.getOutput().get(i).getDeviceType() == 5) {
                                            if (DistributionTransferOverLay.getItems().contains(transformerSectionId.get(findDeviceModel.getOutput().get(i).getDeviceNumber()))) {
                                                DistributionTransferOverLay.getItems().remove(transformerSectionId.get(findDeviceModel.getOutput().get(i).getDeviceNumber()));

                                                if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                                    if (secNodeSectionId.get(findDeviceModel.getOutput().get(i).getSectionId()) != null) {
                                                        sectionFolderOverLay.getItems().remove(secNodeSectionId.get(findDeviceModel.getOutput().get(i).getSectionId()));
                                                    }
                                                }

                                                List<Integer> deviceTypeList = new ArrayList<>();
                                                for (int j = 0; j < findDeviceModel.getOutput().size(); j++) {
                                                    deviceTypeList.add(findDeviceModel.getOutput().get(j).getDeviceType());
                                                }

                                                if (!deviceTypeList.contains(1) || !deviceTypeList.contains(2) || !deviceTypeList.contains(23)) {
                                                    if (nodeOverLay.getItems().contains(NodeId.get(findDeviceModel.getOutput().get(i).getToNodeId()))) {
                                                        nodeOverLay.getItems().remove(NodeId.get(findDeviceModel.getOutput().get(i).getToNodeId()));
                                                    }
                                                }
                                                binding.map.invalidate();

                                                for (int j = 0; j < continentList.size(); j++) {
                                                    if (continentList.get(j).getName().contains("Two-Winding Transformer")) {
                                                        List<DType> deviceList = continentList.get(j).getDeviceList();
                                                        String targetDeviceNumber = findDeviceModel.getOutput().get(i).getDeviceNumber();

                                                        Iterator<DType> iterator = deviceList.iterator();
                                                        while (iterator.hasNext()) {
                                                            DType device = iterator.next();
                                                            if (device.getName().equals(targetDeviceNumber)) {
                                                                iterator.remove();
                                                                break;
                                                            }
                                                        }

                                                        continentList.get(j).setName("Two-Winding Transformer (" + deviceList.size() + ")");
                                                        adapter = new ExpandableDeviceAdapter(MapActivity.this, continentList);
                                                        binding.navigationmenu.setAdapter(adapter);
                                                    }
                                                }

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        progressBarLayout.setVisibility(View.GONE);
                        Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
                    }
                } else if (response.code() == 401) {
                    progressBarLayout.setVisibility(View.GONE);
                    prefManager.setIsUserLogin(false);
                    startActivity(new Intent(MapActivity.this, LoginActivity.class));
                    finish();
                } else {
                    progressBarLayout.setVisibility(View.GONE);
                    @SuppressLint("InflateParams")
                    View layout = LayoutInflater.from(MapActivity.this).inflate(R.layout.toast_layout, null);
                    TextView Ok = layout.findViewById(R.id.okBtn);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                    TextView header = layout.findViewById(R.id.headerTv);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                    TextView description = layout.findViewById(R.id.descripTv);
                    header.setText(response.message() + " - " + response.code());
                    description.setText(getString(R.string.error_msg));
                    Ok.setOnClickListener(v -> {
                        DeleteSection(delSectionId, delDeviceNumber, delDeviceType, delNetworkId);
                    });
                    Toast toast = new Toast(MapActivity.this);
                    toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<DeleteFeature> call, @NonNull Throwable t) {
                progressBarLayout.setVisibility(View.GONE);
                @SuppressLint("InflateParams")
                View layout = LayoutInflater.from(MapActivity.this).inflate(R.layout.toast_layout, null);
                TextView Ok = layout.findViewById(R.id.okBtn);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                TextView header = layout.findViewById(R.id.headerTv);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                TextView description = layout.findViewById(R.id.descripTv);
                header.setText(getString(R.string.error));
                description.setText(getString(R.string.error_msg));
                Ok.setOnClickListener(v -> {
                    DeleteSection(delSectionId, delDeviceNumber, delDeviceType, delNetworkId);
                });
                Toast toast = new Toast(MapActivity.this);
                toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            }
        });
    }

    private void getFeatures(String delSectionId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("DeviceNumber", delSectionId);
        jsonObject.addProperty("DeviceType", "40");
        jsonObject.addProperty("UserType", prefManager.getUserType());
        jsonObject.addProperty("CYMDBNET", prefManager.getDBName());
        Retrofit retrofit = RetrofitClient.getClient(this);
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<FindDeviceModel> call = apiInterface.FindDevice(jsonObject);
        Log.d("FindDevice", jsonObject.toString());
        call.enqueue(new Callback<FindDeviceModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<FindDeviceModel> call, @NonNull Response<FindDeviceModel> response) {
                if (response.code() == 200) {
                    try {
                        progressBarLayout.setVisibility(View.GONE);
                        findDeviceModel = response.body();
                        assert findDeviceModel != null;
                        DeleteSection(DelSectionId, DelDeviceNumber, DelDeviceType, DelNetworkId);
                    } catch (Exception e) {
                        Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
                    }
                } else if (response.code() == 401) {
                    progressBarLayout.setVisibility(View.GONE);
                    prefManager.setIsUserLogin(false);
                    startActivity(new Intent(MapActivity.this, LoginActivity.class));
                    finish();
                } else {
                    progressBarLayout.setVisibility(View.GONE);
                    @SuppressLint("InflateParams")
                    View layout = LayoutInflater.from(MapActivity.this).inflate(R.layout.toast_layout, null);
                    TextView Ok = layout.findViewById(R.id.okBtn);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                    TextView header = layout.findViewById(R.id.headerTv);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                    TextView description = layout.findViewById(R.id.descripTv);
                    header.setText(response.message() + " - " + response.code());
                    description.setText(getString(R.string.error_msg));
                    Ok.setOnClickListener(v -> {
                        getFeatures(delSectionId);
                    });
                    Toast toast = new Toast(MapActivity.this);
                    toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<FindDeviceModel> call, @NonNull Throwable t) {
                progressBarLayout.setVisibility(View.GONE);
                @SuppressLint("InflateParams")
                View layout = LayoutInflater.from(MapActivity.this).inflate(R.layout.toast_layout, null);
                TextView Ok = layout.findViewById(R.id.okBtn);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                TextView header = layout.findViewById(R.id.headerTv);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                TextView description = layout.findViewById(R.id.descripTv);
                header.setText(getString(R.string.error));
                description.setText(getString(R.string.error_msg));
                Ok.setOnClickListener(v -> {
                    getFeatures(delSectionId);
                });
                Toast toast = new Toast(MapActivity.this);
                toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            }
        });
    }

    private void AddDevices() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (CircuitBreakerOverLay != null && !binding.map.getOverlays().contains(CircuitBreakerOverLay)) {
                    binding.map.getOverlayManager().add(CircuitBreakerOverLay);
                }

                if (DistributionTransferOverLay != null && !binding.map.getOverlays().contains(DistributionTransferOverLay)) {
                    binding.map.getOverlayManager().add(DistributionTransferOverLay);
                }

                if (FuseOverLay != null && !binding.map.getOverlays().contains(FuseOverLay)) {
                    binding.map.getOverlayManager().add(FuseOverLay);
                }

                if (SwitchOverLay != null && !binding.map.getOverlays().contains(SwitchOverLay)) {
                    binding.map.getOverlayManager().add(SwitchOverLay);
                }

                if (ShuntCapacitorOverLay != null && !binding.map.getOverlays().contains(ShuntCapacitorOverLay)) {
                    binding.map.getOverlayManager().add(ShuntCapacitorOverLay);
                }

                if (SpotLoadOverLay != null && !binding.map.getOverlays().contains(SpotLoadOverLay)) {
                    binding.map.getOverlayManager().add(SpotLoadOverLay);
                }

            }
        });
    }

    private void AddNode() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (nodeOverLay != null && !binding.map.getOverlays().contains(nodeOverLay)) {
                    binding.map.getOverlayManager().add(nodeOverLay);
                }
            }
        });
    }

    private void RemoveDevices() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (binding.map.getOverlays().contains(CircuitBreakerOverLay)) {
                    binding.map.getOverlayManager().remove(CircuitBreakerOverLay);
                }

                if (binding.map.getOverlays().contains(DistributionTransferOverLay)) {
                    binding.map.getOverlayManager().remove(DistributionTransferOverLay);
                }

                if (binding.map.getOverlays().contains(FuseOverLay)) {
                    binding.map.getOverlayManager().remove(FuseOverLay);
                }

                if (binding.map.getOverlays().contains(SwitchOverLay)) {
                    binding.map.getOverlayManager().remove(SwitchOverLay);
                }

                if (binding.map.getOverlays().contains(ShuntCapacitorOverLay)) {
                    binding.map.getOverlayManager().remove(ShuntCapacitorOverLay);
                }

                if (binding.map.getOverlays().contains(SpotLoadOverLay)) {
                    binding.map.getOverlayManager().remove(SpotLoadOverLay);
                }
            }
        });
    }

    private void RemoveNode() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (binding.map.getOverlays().contains(nodeOverLay)) {
                    binding.map.getOverlayManager().remove(nodeOverLay);
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void getNetworkData(String feederId) {
        progressBarLayout.setProcessText("Reading Files...");
        progressBarLayout.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("NetworkName", feederId);
                    jsonObject.addProperty("UserType", prefManager.getUserType());
                    jsonObject.addProperty("CYMDBNET", prefManager.getDBName());
                    Retrofit retrofit = RetrofitClient.getClient(MapActivity.this);
                    ApiInterface apiInterface = retrofit.create(ApiInterface.class);
                    Call<JsonObject> call = apiInterface.getNetworkData("networkdata/", jsonObject);
                    call.enqueue(new Callback<JsonObject>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                            if (response.code() == 200) {
                                try {
                                    progressBarLayout.setVisibility(View.GONE);
                                    JsonObject jsonObject1 = response.body();
                                    while (!ResponseDataUtils.NetworkList.isEmpty()) {
                                        ResponseDataUtils.NetworkList.clear();
                                    }
                                    assert jsonObject1 != null;
                                    JSONObject jsonObject2 = new JSONObject(jsonObject1.toString());
                                    new AddGeoJsonData(binding.map, jsonObject2).execute();
                                    selectedFeeder.remove(0);
                                } catch (JSONException e) {
                                    Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
                                }
                            } else if (response.code() == 401) {
                                progressBarLayout.setVisibility(View.GONE);
                                prefManager.setIsUserLogin(false);
                                startActivity(new Intent(MapActivity.this, LoginActivity.class));
                                finish();
                            } else {
                                progressBarLayout.setVisibility(View.GONE);
                                @SuppressLint("InflateParams")
                                View layout = LayoutInflater.from(MapActivity.this).inflate(R.layout.toast_layout, null);
                                TextView Ok = layout.findViewById(R.id.okBtn);
                                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                                TextView header = layout.findViewById(R.id.headerTv);
                                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                                TextView description = layout.findViewById(R.id.descripTv);
                                header.setText(response.message() + " - " + response.code());
                                description.setText(getString(R.string.error_msg));
                                Ok.setOnClickListener(v -> {
                                    getNetworkData(feederId);
                                });
                                Toast toast = new Toast(MapActivity.this);
                                toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                                toast.setDuration(Toast.LENGTH_LONG);
                                toast.setView(layout);
                                toast.show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                            progressBarLayout.setVisibility(View.GONE);
                            @SuppressLint("InflateParams")
                            View layout = LayoutInflater.from(MapActivity.this).inflate(R.layout.toast_layout, null);
                            TextView Ok = layout.findViewById(R.id.okBtn);
                            @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                            TextView header = layout.findViewById(R.id.headerTv);
                            @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                            TextView description = layout.findViewById(R.id.descripTv);
                            header.setText(getString(R.string.error));
                            description.setText(getString(R.string.error_msg));
                            Ok.setOnClickListener(v -> {
                                getNetworkData(feederId);
                            });
                            Toast toast = new Toast(MapActivity.this);
                            toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();
                        }
                    });
                } catch (Exception e) {
                    Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
                }
            }
        }).start();
    }

    @SuppressLint("SetTextI18n")
    private void getSurveyNetworkData(String str) {
        progressBarLayout.setProcessText("Reading Files...");
        progressBarLayout.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("NetworkName", str);
                    jsonObject.addProperty("UserType", prefManager.getUserType());
                    jsonObject.addProperty("CYMDBNET", prefManager.getDBName());
                    Retrofit retrofit = RetrofitClient.getClient(MapActivity.this);
                    ApiInterface apiInterface = retrofit.create(ApiInterface.class);
                    Call<JsonObject> call = apiInterface.getNetworkData("networkdata/", jsonObject);
                    call.enqueue(new Callback<JsonObject>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                            if (response.code() == 200) {
                                try {
                                    progressBarLayout.setVisibility(View.GONE);
                                    JsonObject jsonObject1 = response.body();
                                    while (!ResponseDataUtils.NetworkList.isEmpty()) {
                                        ResponseDataUtils.NetworkList.clear();
                                    }
                                    prefManager.setCable(null);
                                    prefManager.setOverhead(null);
                                    prefManager.setUnBalenced(null);
                                    prefManager.setTransformer(null);
                                    prefManager.setFuse(null);
                                    prefManager.setSwitch(null);
                                    prefManager.setBreaker(null);
                                    prefManager.setShuntCapacitor(null);
                                    assert jsonObject1 != null;
                                    JSONObject jsonObject2 = new JSONObject(jsonObject1.toString());
                                    new AddGeoJsonData(binding.map, jsonObject2).execute();
                                } catch (JSONException e) {
                                    Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
                                }
                            } else if (response.code() == 401) {
                                progressBarLayout.setVisibility(View.GONE);
                                prefManager.setIsUserLogin(false);
                                startActivity(new Intent(MapActivity.this, LoginActivity.class));
                                finish();
                            } else {
                                progressBarLayout.setVisibility(View.GONE);
                                @SuppressLint("InflateParams")
                                View layout = LayoutInflater.from(MapActivity.this).inflate(R.layout.toast_layout, null);
                                TextView Ok = layout.findViewById(R.id.okBtn);
                                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                                TextView header = layout.findViewById(R.id.headerTv);
                                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                                TextView description = layout.findViewById(R.id.descripTv);
                                header.setText(response.message() + " - " + response.code());
                                description.setText(getString(R.string.error_msg));
                                Ok.setOnClickListener(v -> {
                                    getSurveyNetworkData(str);
                                });
                                Toast toast = new Toast(MapActivity.this);
                                toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                                toast.setDuration(Toast.LENGTH_LONG);
                                toast.setView(layout);
                                toast.show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                            progressBarLayout.setVisibility(View.GONE);
                            @SuppressLint("InflateParams")
                            View layout = LayoutInflater.from(MapActivity.this).inflate(R.layout.toast_layout, null);
                            TextView Ok = layout.findViewById(R.id.okBtn);
                            @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                            TextView header = layout.findViewById(R.id.headerTv);
                            @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                            TextView description = layout.findViewById(R.id.descripTv);
                            header.setText(getString(R.string.error));
                            description.setText(getString(R.string.error_msg));
                            Ok.setOnClickListener(v -> {
                                getSurveyNetworkData(str);
                            });
                            Toast toast = new Toast(MapActivity.this);
                            toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();
                        }
                    });
                } catch (Exception e) {
                    Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
                }
            }
        }).start();
    }

    private void getConsumerData() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("Type", "ConsumerClass");
        jsonObject.addProperty("CYMDBNET", prefManager.getDBName());
        Retrofit retrofit = RetrofitClient.getClient(MapActivity.this);
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<ConsumerModel> call = apiInterface.consumerData(jsonObject);
        call.enqueue(new Callback<ConsumerModel>() {
            @Override
            public void onResponse(Call<ConsumerModel> call, Response<ConsumerModel> response) {
                if (response.code() == 200) {
                    ConsumerModel consumerModel = response.body();
                    if (consumerModel.getConsumerClassID() != null && !consumerModel.getConsumerClassID().isEmpty()) {
                        prefManager.saveConsumerClasses(consumerModel.getConsumerClassID());
                    }
                } else {
                    Snackbar.make(binding.getRoot(), response.code() + getString(R.string.error_msg), Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getConsumerData();
                        }
                    }).show();
                }
            }

            @Override
            public void onFailure(Call<ConsumerModel> call, Throwable t) {
                Snackbar.make(binding.getRoot(), getString(R.string.error_msg), Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getConsumerData();
                    }
                }).show();
            }
        });
    }

    private void loadFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        clearBackStack();
        fragmentManager.beginTransaction()
                .replace(R.id.bottomContainer, fragment, tag)
                .addToBackStack(null)
                .commit();
    }

    public void clearBackStack() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = fragmentManager.getBackStackEntryAt(0);
            fragmentManager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    private void addNewSectionDevices(String sectionType, JsonObject jsonObject) {
        progressBarLayout.setProcessText("Add New Devices");
        progressBarLayout.setVisibility(View.VISIBLE);
        Retrofit retrofit = RetrofitClient.getClient(MapActivity.this);
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getNetworkData("SurveyData/", jsonObject);
        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.code() == 200) {
                    try {
                        progressBarLayout.setVisibility(View.GONE);
                        JsonObject responseData = response.body();
                        assert responseData != null;
                        JSONObject responseData1 = new JSONObject(responseData.toString());

                        if (!responseData1.getJSONObject("output").getJSONObject("cables_data2").getJSONArray("features").toString().contains("[]")) {
                            if (CableKml != null) {
                                CableKml.parseGeoJSON(String.valueOf(responseData1.getJSONObject("output").getJSONObject("cables_data2")));
                                KmlFeature.Styler styler = new MyKmlStyler(Color.RED, binding.map);
                                FolderOverlay folderOverlay = (FolderOverlay) CableKml.mKmlRoot.buildOverlay(binding.map, null, styler, CableKml);
                                for (Overlay item : folderOverlay.getItems()) {
                                    CableFolderOverLay.add(item);
                                    binding.map.invalidate();
                                }
                                for (int j = 0; j < responseData1.getJSONObject("output").getJSONObject("cables_data2").getJSONArray("features").length(); j++) {
                                    CaObject.getJSONArray("features").put(responseData1.getJSONObject("output").getJSONObject("cables_data2").getJSONArray("features").get(j));
                                }
                            } else {
                                CaObject = new JSONObject(responseData1.getJSONObject("output").getJSONObject("cables_data2").toString());
                                CableKml = new KmlDocument();
                                CableKml.parseGeoJSON(String.valueOf(responseData1.getJSONObject("output").getJSONObject("cables_data2")));
                                KmlFeature.Styler styler = new MyKmlStyler(Color.RED, binding.map);
                                CableFolderOverLay = (FolderOverlay) CableKml.mKmlRoot.buildOverlay(binding.map, null, styler, CableKml);
                                binding.map.getOverlays().add(CableFolderOverLay);
                                binding.map.invalidate();
                            }

                            boolean isCable = false;

                            for (int i = 0; i < continentList.size(); i++) {
                                if (continentList.get(i).getName().contains("Cable")) {
                                    DType type = new DType(responseData1.getJSONObject("output").getJSONObject("cables_data2").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("DeviceNumber"), Integer.parseInt(responseData1.getJSONObject("output").getJSONObject("cables_data2").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("DeviceType")));
                                    continentList.get(i).getDeviceList().add(type);
                                    continentList.get(i).setName("Cable" + " " + "(" + continentList.get(i).getDeviceList().size() + ")");
                                    isCable = true;
                                }
                            }

                            if (!isCable) {
                                ArrayList<DType> list = new ArrayList<>();
                                DType type = new DType(responseData1.getJSONObject("output").getJSONObject("cables_data2").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("DeviceNumber"), Integer.parseInt(responseData1.getJSONObject("output").getJSONObject("cables_data2").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("DeviceType")));
                                list.add(type);
                                Continent continent = new Continent("Cable" + " " + "(" + list.size() + ")", list);
                                continentList.add(continent);
                            }
                            adapter = new ExpandableDeviceAdapter(MapActivity.this, continentList);
                            binding.navigationmenu.setAdapter(adapter);
                        }

                        if (!responseData1.getJSONObject("output").getJSONObject("oh_data2").getJSONArray("features").toString().contains("[]")) {
                            KmlFeature.Styler styler = new OverHeadKmlStyler(Color.BLUE, binding.map);
                            if (OverHeadKml != null) {
                                OverHeadKml.parseGeoJSON(String.valueOf(responseData1.getJSONObject("output").getJSONObject("oh_data2")));
                                FolderOverlay folderOverlay = (FolderOverlay) OverHeadKml.mKmlRoot.buildOverlay(binding.map, null, styler, OverHeadKml);
                                for (Overlay item : folderOverlay.getItems()) {
                                    OverheadFolderOverLay.add(item);
                                    binding.map.invalidate();
                                }
                                for (int j = 0; j < responseData1.getJSONObject("output").getJSONObject("oh_data2").getJSONArray("features").length(); j++) {
                                    OhObject.getJSONArray("features").put(responseData1.getJSONObject("output").getJSONObject("oh_data2").getJSONArray("features").get(j));
                                }
                            } else {
                                OhObject = new JSONObject(responseData1.getJSONObject("output").getJSONObject("oh_data2").toString());
                                OverHeadKml = new KmlDocument();
                                OverHeadKml.parseGeoJSON(String.valueOf(responseData1.getJSONObject("output").getJSONObject("oh_data2")));
                                OverheadFolderOverLay = (FolderOverlay) OverHeadKml.mKmlRoot.buildOverlay(binding.map, null, styler, OverHeadKml);
                                binding.map.getOverlays().add(OverheadFolderOverLay);
                                binding.map.invalidate();
                            }

                            boolean isOverhead = false;

                            for (int i = 0; i < continentList.size(); i++) {
                                if (continentList.get(i).getName().contains("Overhead Balance")) {
                                    DType type = new DType(responseData1.getJSONObject("output").getJSONObject("oh_data2").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("DeviceNumber"), Integer.parseInt(responseData1.getJSONObject("output").getJSONObject("oh_data2").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("DeviceType")));
                                    continentList.get(i).getDeviceList().add(type);
                                    continentList.get(i).setName("Overhead Balance" + " " + "(" + continentList.get(i).getDeviceList().size() + ")");
                                    isOverhead = true;
                                }
                            }

                            if (!isOverhead) {
                                ArrayList<DType> list = new ArrayList<>();
                                DType type = new DType(responseData1.getJSONObject("output").getJSONObject("oh_data2").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("DeviceNumber"), Integer.parseInt(responseData1.getJSONObject("output").getJSONObject("oh_data2").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("DeviceType")));
                                list.add(type);
                                Continent continent = new Continent("Overhead Balance" + " " + "(" + list.size() + ")", list);
                                continentList.add(continent);
                            }

                            adapter = new ExpandableDeviceAdapter(MapActivity.this, continentList);
                            binding.navigationmenu.setAdapter(adapter);

                        }

                        if (!responseData1.getJSONObject("output").getJSONObject("ohunbal_data2").getJSONArray("features").toString().contains("[]")) {
                            if (UnBalencedKml != null) {
                                UnBalencedKml.parseGeoJSON(String.valueOf(responseData1.getJSONObject("output").getJSONObject("ohunbal_data2")));
                                KmlFeature.Styler styler = new UnbalanceKmlStyler(Color.BLACK, binding.map);
                                FolderOverlay folderOverlay = (FolderOverlay) UnBalencedKml.mKmlRoot.buildOverlay(binding.map, null, styler, UnBalencedKml);
                                for (Overlay item : folderOverlay.getItems()) {
                                    UnBalanceFolderOverLay.add(item);
                                    binding.map.invalidate();
                                }
                                for (int j = 0; j < responseData1.getJSONObject("output").getJSONObject("ohunbal_data2").getJSONArray("features").length(); j++) {
                                    UnBalObject.getJSONArray("features").put(responseData1.getJSONObject("output").getJSONObject("ohunbal_data2").getJSONArray("features").get(j));
                                }
                            } else {
                                UnBalObject = new JSONObject(responseData1.getJSONObject("output").getJSONObject("ohunbal_data2").toString());
                                UnBalencedKml = new KmlDocument();
                                UnBalencedKml.parseGeoJSON(String.valueOf(responseData1.getJSONObject("output").getJSONObject("ohunbal_data2")));
                                KmlFeature.Styler styler = new UnbalanceKmlStyler(Color.BLACK, binding.map);
                                UnBalanceFolderOverLay = (FolderOverlay) UnBalencedKml.mKmlRoot.buildOverlay(binding.map, null, styler, UnBalencedKml);
                                binding.map.getOverlays().add(UnBalanceFolderOverLay);
                                binding.map.invalidate();
                            }

                            boolean isUnBalenced = false;

                            for (int i = 0; i < continentList.size(); i++) {
                                if (continentList.get(i).getName().contains("Unbalance")) {
                                    DType type = new DType(responseData1.getJSONObject("output").getJSONObject("ohunbal_data2").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("DeviceNumber"), Integer.parseInt(responseData1.getJSONObject("output").getJSONObject("ohunbal_data2").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("DeviceType")));
                                    continentList.get(i).getDeviceList().add(type);
                                    continentList.get(i).setName("Unbalance" + " " + "(" + continentList.get(i).getDeviceList().size() + ")");
                                    isUnBalenced = true;
                                }
                            }

                            if (!isUnBalenced) {
                                ArrayList<DType> list = new ArrayList<>();
                                DType type = new DType(responseData1.getJSONObject("output").getJSONObject("ohunbal_data2").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("DeviceNumber"), Integer.parseInt(responseData1.getJSONObject("output").getJSONObject("ohunbal_data2").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("DeviceType")));
                                list.add(type);
                                Continent continent = new Continent("Unbalance" + " " + "(" + list.size() + ")", list);
                                continentList.add(continent);
                            }

                            adapter = new ExpandableDeviceAdapter(MapActivity.this, continentList);
                            binding.navigationmenu.setAdapter(adapter);

                        }

                        if (!responseData1.getJSONObject("output").getJSONObject("sectionnodev").getJSONArray("features").toString().equals("[]")) {
                            if (sectionNodeKml != null) {
                                sectionNodeKml.parseGeoJSON(String.valueOf(responseData1.getJSONObject("output").getJSONObject("sectionnodev")));
                                KmlFeature.Styler styler = new SectionNodeKmlStyler(Color.BLACK, binding.map);
                                FolderOverlay folderOverlay = (FolderOverlay) sectionNodeKml.mKmlRoot.buildOverlay(binding.map, null, styler, sectionNodeKml);
                                for (Overlay item : folderOverlay.getItems()) {
                                    sectionFolderOverLay.add(item);
                                }

                                for (int j = 0; j < responseData1.getJSONObject("output").getJSONObject("sectionnodev").getJSONArray("features").length(); j++) {
                                    SecNodeObject.getJSONArray("features").put(responseData1.getJSONObject("output").getJSONObject("sectionnodev").getJSONArray("features").get(j));
                                }

                                for (int i = 0; i < SecNodeObject.getJSONArray("features").length(); i++) {
                                    spLineSectionList.add(SecNodeObject.getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceNumber"));
                                }

                            } else {
                                SecNodeObject = new JSONObject(String.valueOf(responseData1.getJSONObject("output").getJSONObject("sectionnodev")));
                                sectionNodeKml = new KmlDocument();
                                sectionNodeKml.parseGeoJSON(String.valueOf(SecNodeObject));
                                KmlFeature.Styler styler = new SectionNodeKmlStyler(Color.BLACK, binding.map);
                                sectionFolderOverLay = (FolderOverlay) sectionNodeKml.mKmlRoot.buildOverlay(binding.map, null, styler, sectionNodeKml);
                                binding.map.getOverlays().add(sectionFolderOverLay);

                                for (int i = 0; i < SecNodeObject.getJSONArray("features").length(); i++) {
                                    spLineSectionList.add(SecNodeObject.getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceNumber"));
                                }

                            }
                        }

                        if (!responseData1.getJSONObject("output").getJSONObject("cb_data2").getJSONArray("features").toString().contains("[]")) {
                            if (BreakarKml != null) {
                                BreakarKml.parseGeoJSON(responseData1.getJSONObject("output").getJSONObject("cb_data2").toString());
                                KmlFeature.Styler styler = new circuitBreakerKmlStyler(Color.BLUE, binding.map);
                                FolderOverlay folderOverlay = (FolderOverlay) BreakarKml.mKmlRoot.buildOverlay(binding.map, null, styler, BreakarKml);
                                for (Overlay item : folderOverlay.getItems()) {
                                    CircuitBreakerOverLay.add(item);
                                }
                                binding.map.invalidate();
                            } else {
                                BreakarKml = new KmlDocument();
                                BreakarKml.parseGeoJSON(responseData1.getJSONObject("output").getJSONObject("cb_data2").toString());
                                KmlFeature.Styler styler = new circuitBreakerKmlStyler(Color.BLUE, binding.map);
                                CircuitBreakerOverLay = (FolderOverlay) BreakarKml.mKmlRoot.buildOverlay(binding.map, null, styler, BreakarKml);
                                binding.map.invalidate();
                            }

                            boolean isBreaker = false;

                            for (int i = 0; i < continentList.size(); i++) {
                                if (continentList.get(i).getName().contains("Breaker")) {
                                    DType type = new DType(responseData1.getJSONObject("output").getJSONObject("cb_data2").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("DeviceNumber"), Integer.parseInt(responseData1.getJSONObject("output").getJSONObject("cb_data2").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("DeviceType")));
                                    continentList.get(i).getDeviceList().add(type);
                                    continentList.get(i).setName("Breaker" + " " + "(" + continentList.get(i).getDeviceList().size() + ")");
                                    isBreaker = true;
                                }
                            }

                            if (!isBreaker) {
                                ArrayList<DType> list = new ArrayList<>();
                                DType type = new DType(responseData1.getJSONObject("output").getJSONObject("cb_data2").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("DeviceNumber"), Integer.parseInt(responseData1.getJSONObject("output").getJSONObject("cb_data2").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("DeviceType")));
                                list.add(type);
                                Continent continent = new Continent("Breaker" + " " + "(" + list.size() + ")", list);
                                continentList.add(continent);
                            }

                            adapter = new ExpandableDeviceAdapter(MapActivity.this, continentList);
                            binding.navigationmenu.setAdapter(adapter);
                        }

                        if (!responseData1.getJSONObject("output").getJSONObject("switch_data2").getJSONArray("features").toString().contains("[]")) {
                            if (SwitchKml != null) {
                                SwitchKml.parseGeoJSON(responseData1.getJSONObject("output").getJSONObject("switch_data2").toString());
                                KmlFeature.Styler styler = new SwitchKmlStyler(Color.BLUE, binding.map);
                                FolderOverlay folderOverlay = (FolderOverlay) SwitchKml.mKmlRoot.buildOverlay(binding.map, null, styler, SwitchKml);
                                for (Overlay item : folderOverlay.getItems()) {
                                    SwitchOverLay.add(item);
                                }
                                binding.map.invalidate();
                            } else {
                                SwitchKml = new KmlDocument();
                                SwitchKml.parseGeoJSON(responseData1.getJSONObject("output").getJSONObject("switch_data2").toString());
                                KmlFeature.Styler styler = new SwitchKmlStyler(Color.BLUE, binding.map);
                                SwitchOverLay = (FolderOverlay) SwitchKml.mKmlRoot.buildOverlay(binding.map, null, styler, SwitchKml);
                            }

                            boolean isSwitch = false;

                            for (int i = 0; i < continentList.size(); i++) {
                                if (continentList.get(i).getName().contains("Switch")) {
                                    DType type = new DType(responseData1.getJSONObject("output").getJSONObject("switch_data2").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("DeviceNumber"), Integer.parseInt(responseData1.getJSONObject("output").getJSONObject("switch_data2").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("DeviceType")));
                                    continentList.get(i).getDeviceList().add(type);
                                    continentList.get(i).setName("Switch" + " " + "(" + continentList.get(i).getDeviceList().size() + ")");
                                    isSwitch = true;
                                }
                            }

                            if (!isSwitch) {
                                ArrayList<DType> list = new ArrayList<>();
                                DType type = new DType(responseData1.getJSONObject("output").getJSONObject("switch_data2").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("DeviceNumber"), Integer.parseInt(responseData1.getJSONObject("output").getJSONObject("switch_data2").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("DeviceType")));
                                list.add(type);
                                Continent continent = new Continent("Switch" + " " + "(" + list.size() + ")", list);
                                continentList.add(continent);
                            }

                            adapter = new ExpandableDeviceAdapter(MapActivity.this, continentList);
                            binding.navigationmenu.setAdapter(adapter);
                        }

                        if (!responseData1.getJSONObject("output").getJSONObject("fuse_data2").getJSONArray("features").toString().contains("[]")) {
                            if (FuseKml != null) {
                                FuseKml.parseGeoJSON(responseData1.getJSONObject("output").getJSONObject("fuse_data2").toString());
                                KmlFeature.Styler styler = new FuseKmlStyler(Color.BLUE, binding.map);
                                FolderOverlay folderOverlay = (FolderOverlay) FuseKml.mKmlRoot.buildOverlay(binding.map, null, styler, FuseKml);
                                for (Overlay item : folderOverlay.getItems()) {
                                    FuseOverLay.add(item);
                                }
                            } else {
                                FuseKml = new KmlDocument();
                                FuseKml.parseGeoJSON(responseData1.getJSONObject("output").getJSONObject("fuse_data2").toString());
                                KmlFeature.Styler styler = new FuseKmlStyler(Color.BLUE, binding.map);
                                FuseOverLay = (FolderOverlay) FuseKml.mKmlRoot.buildOverlay(binding.map, null, styler, FuseKml);
                            }

                            boolean isFuse = false;

                            for (int i = 0; i < continentList.size(); i++) {
                                if (continentList.get(i).getName().contains("Fuse")) {
                                    DType type = new DType(responseData1.getJSONObject("output").getJSONObject("fuse_data2").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("DeviceNumber"), Integer.parseInt(responseData1.getJSONObject("output").getJSONObject("fuse_data2").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("DeviceType")));
                                    continentList.get(i).getDeviceList().add(type);
                                    continentList.get(i).setName("Fuse" + " " + "(" + continentList.get(i).getDeviceList().size() + ")");
                                    isFuse = true;
                                }
                            }

                            if (!isFuse) {
                                ArrayList<DType> list = new ArrayList<>();
                                DType type = new DType(responseData1.getJSONObject("output").getJSONObject("fuse_data2").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("DeviceNumber"), Integer.parseInt(responseData1.getJSONObject("output").getJSONObject("fuse_data2").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("DeviceType")));
                                list.add(type);
                                Continent continent = new Continent("Fuse" + " " + "(" + list.size() + ")", list);
                                continentList.add(continent);
                            }

                            adapter = new ExpandableDeviceAdapter(MapActivity.this, continentList);
                            binding.navigationmenu.setAdapter(adapter);
                        }

                        if (!responseData1.getJSONObject("output").getJSONObject("dt_data2").getJSONArray("features").toString().contains("[]")) {
                            if (TransformerKml != null) {
                                TransformerKml.parseGeoJSON(responseData1.getJSONObject("output").getJSONObject("dt_data2").toString());
                                KmlFeature.Styler styler = new DistributionTransferKmlStyler(Color.BLUE, binding.map);
                                FolderOverlay folderOverlay = (FolderOverlay) TransformerKml.mKmlRoot.buildOverlay(binding.map, null, styler, TransformerKml);
                                for (Overlay item : folderOverlay.getItems()) {
                                    DistributionTransferOverLay.add(item);
                                }
                            } else {
                                TransformerKml = new KmlDocument();
                                TransformerKml.parseGeoJSON(responseData1.getJSONObject("output").getJSONObject("dt_data2").toString());
                                KmlFeature.Styler styler = new DistributionTransferKmlStyler(Color.BLUE, binding.map);
                                DistributionTransferOverLay = (FolderOverlay) TransformerKml.mKmlRoot.buildOverlay(binding.map, null, styler, TransformerKml);
                            }

                            boolean isTransformer = false;

                            for (int i = 0; i < continentList.size(); i++) {
                                if (continentList.get(i).getName().contains("Two-Winding Transformer")) {
                                    DType type = new DType(responseData1.getJSONObject("output").getJSONObject("dt_data2").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("DeviceNumber") + " (" + responseData1.getJSONObject("output").getJSONObject("dt_data2").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("DTCName") + ")", Integer.parseInt(responseData1.getJSONObject("output").getJSONObject("dt_data2").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("DeviceType")));
                                    continentList.get(i).getDeviceList().add(type);
                                    continentList.get(i).setName("Two-Winding Transformer" + " " + "(" + continentList.get(i).getDeviceList().size() + ")");
                                    isTransformer = true;
                                }
                            }

                            if (!isTransformer) {
                                ArrayList<DType> list = new ArrayList<>();
                                DType type = new DType(responseData1.getJSONObject("output").getJSONObject("dt_data2").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("DeviceNumber") + " (" + responseData1.getJSONObject("output").getJSONObject("dt_data2").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("DTCName") + ")", Integer.parseInt(responseData1.getJSONObject("output").getJSONObject("dt_data2").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("DeviceType")));
                                list.add(type);
                                Continent continent = new Continent("Two-Winding Transformer" + " " + "(" + list.size() + ")", list);
                                continentList.add(continent);
                            }

                            adapter = new ExpandableDeviceAdapter(MapActivity.this, continentList);
                            binding.navigationmenu.setAdapter(adapter);
                        }

                        if (!responseData1.getJSONObject("output").getJSONObject("shunt_capacitor2").getJSONArray("features").toString().contains("[]")) {
                            if (ShuntCapacitorKml != null) {
                                ShuntCapacitorKml.parseGeoJSON(responseData1.getJSONObject("output").getJSONObject("shunt_capacitor2").toString());
                                KmlFeature.Styler styler = new ShuntCapacitorKmlStyler(Color.BLUE, binding.map);
                                FolderOverlay folderOverlay = (FolderOverlay) ShuntCapacitorKml.mKmlRoot.buildOverlay(binding.map, null, styler, ShuntCapacitorKml);
                                for (Overlay item : folderOverlay.getItems()) {
                                    ShuntCapacitorOverLay.add(item);
                                }
                            } else {
                                ShuntCapacitorKml = new KmlDocument();
                                ShuntCapacitorKml.parseGeoJSON(responseData1.getJSONObject("output").getJSONObject("shunt_capacitor2").toString());
                                KmlFeature.Styler styler = new ShuntCapacitorKmlStyler(Color.BLUE, binding.map);
                                ShuntCapacitorOverLay = (FolderOverlay) ShuntCapacitorKml.mKmlRoot.buildOverlay(binding.map, null, styler, ShuntCapacitorKml);
                            }

                            boolean isCapacitor = false;

                            for (int i = 0; i < continentList.size(); i++) {
                                if (continentList.get(i).getName().contains("Shunt Capacitor")) {
                                    DType type = new DType(responseData1.getJSONObject("output").getJSONObject("shunt_capacitor2").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("DeviceNumber"), Integer.parseInt(responseData1.getJSONObject("output").getJSONObject("shunt_capacitor2").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("DeviceType")));
                                    continentList.get(i).getDeviceList().add(type);
                                    continentList.get(i).setName("Shunt Capacitor" + " " + "(" + continentList.get(i).getDeviceList().size() + ")");
                                    isCapacitor = true;
                                }
                            }

                            if (!isCapacitor) {
                                ArrayList<DType> list = new ArrayList<>();
                                DType type = new DType(responseData1.getJSONObject("output").getJSONObject("shunt_capacitor2").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("DeviceNumber"), Integer.parseInt(responseData1.getJSONObject("output").getJSONObject("shunt_capacitor2").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("DeviceType")));
                                list.add(type);
                                Continent continent = new Continent("Shunt Capacitor" + " " + "(" + list.size() + ")", list);
                                continentList.add(continent);
                            }

                            adapter = new ExpandableDeviceAdapter(MapActivity.this, continentList);
                            binding.navigationmenu.setAdapter(adapter);
                        }

                        if (!responseData1.getJSONObject("output").getJSONObject("spotload2").getJSONArray("features").toString().contains("[]")) {
                            if (SpotloadKml != null) {
                                SpotloadKml.parseGeoJSON(responseData1.getJSONObject("output").getJSONObject("spotload2").toString());
                                KmlFeature.Styler styler = new SpotLoadKmlStyler(Color.BLUE, binding.map);
                                FolderOverlay folderOverlay = (FolderOverlay) SpotloadKml.mKmlRoot.buildOverlay(binding.map, null, styler, SpotloadKml);
                                for (Overlay item : folderOverlay.getItems()) {
                                    SpotLoadOverLay.add(item);
                                }
                            } else {
                                SpotloadKml = new KmlDocument();
                                SpotloadKml.parseGeoJSON(responseData1.getJSONObject("output").getJSONObject("spotload2").toString());
                                KmlFeature.Styler styler = new SpotLoadKmlStyler(Color.BLUE, binding.map);
                                SpotLoadOverLay = (FolderOverlay) SpotloadKml.mKmlRoot.buildOverlay(binding.map, null, styler, SpotloadKml);
                            }

                            boolean isSpotload = false;

                            for (int i = 0; i < continentList.size(); i++) {
                                if (continentList.get(i).getName().contains("SpotLoad")) {
                                    DType type = new DType(responseData1.getJSONObject("output").getJSONObject("spotload2").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("DeviceNumber"), Integer.parseInt(responseData1.getJSONObject("output").getJSONObject("spotload2").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("DeviceType")));
                                    continentList.get(i).getDeviceList().add(type);
                                    continentList.get(i).setName("SpotLoad" + " " + "(" + continentList.get(i).getDeviceList().size() + ")");
                                    isSpotload = true;
                                }
                            }

                            if (!isSpotload) {
                                ArrayList<DType> list = new ArrayList<>();
                                DType type = new DType(responseData1.getJSONObject("output").getJSONObject("spotload2").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("DeviceNumber"), Integer.parseInt(responseData1.getJSONObject("output").getJSONObject("spotload2").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("DeviceType")));
                                list.add(type);
                                Continent continent = new Continent("SpotLoad" + " " + "(" + list.size() + ")", list);
                                continentList.add(continent);
                            }

                            adapter = new ExpandableDeviceAdapter(MapActivity.this, continentList);
                            binding.navigationmenu.setAdapter(adapter);
                        }

                        if (!responseData1.getJSONObject("output").getJSONObject("node_data2").getJSONArray("features").toString().equals("[]")) {
                            KmlFeature.Styler nodeStyler = new NodeKmlStyler(Color.BLUE, binding.map);
                            if (NodeKml != null) {
                                NodeKml.parseGeoJSON(responseData1.getJSONObject("output").getJSONObject("node_data2").toString());
                                FolderOverlay folderOverlay1 = (FolderOverlay) NodeKml.mKmlRoot.buildOverlay(binding.map, null, nodeStyler, NodeKml);
                                for (Overlay item : folderOverlay1.getItems()) {
                                    nodeOverLay.add(item);
                                    binding.map.invalidate();
                                }
                            } else {
                                NodeKml = new KmlDocument();
                                NodeKml.parseGeoJSON(responseData1.getJSONObject("output").getJSONObject("node_data2").toString());
                                nodeOverLay = (FolderOverlay) NodeKml.mKmlRoot.buildOverlay(binding.map, null, nodeStyler, NodeKml);
                                binding.map.getOverlays().add(nodeOverLay);
                                binding.map.invalidate();
                            }
                        }

                        if (sectionType.equals("0")) {
                            if (!responseData1.getJSONObject("output").getJSONObject("HEADNODE").getJSONArray("features").toString().contains("[]")) {
                                KmlDocument SourceKml = new KmlDocument();
                                SourceKml.parseGeoJSON(responseData1.getJSONObject("output").getJSONObject("HEADNODE").toString());
                                KmlFeature.Styler styler = new SourceKmlStyler(Color.BLUE, binding.map);
                                SourceOverLay = (FolderOverlay) SourceKml.mKmlRoot.buildOverlay(binding.map, null, styler, SourceKml);
                                FolderOverlay newSource = (FolderOverlay) SourceKml.mKmlRoot.buildOverlay(binding.map, null, styler, SourceKml);
                                binding.map.getOverlays().add(newSource);
                                isSource = true;
                                binding.addSource.setVisibility(View.GONE);
                                newNetworkID = responseData1.getJSONObject("output").getJSONObject("HEADNODE").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("NetworkId");

                                ArrayList<DType> list = new ArrayList<>();
                                DType type = new DType(responseData1.getJSONObject("output").getJSONObject("HEADNODE").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("NetworkId"), 43);
                                list.add(type);
                                Continent continent = new Continent("Feeder ID" + " " + "(" + list.size() + ")", list);
                                continentList.add(continent);

                                adapter = new ExpandableDeviceAdapter(MapActivity.this, continentList);
                                binding.navigationmenu.setAdapter(adapter);
                            }
                        }

                        clearVertexNode();
                        binding.map.invalidate();

                    } catch (Exception e) {
                        Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
                        clearVertexNode();
                    }
                } else if (response.code() == 401) {
                    progressBarLayout.setVisibility(View.GONE);
                    prefManager.setIsUserLogin(false);
                    startActivity(new Intent(MapActivity.this, LoginActivity.class));
                    finish();
                } else {
                    progressBarLayout.setVisibility(View.GONE);
                    clearVertexNode();
                    @SuppressLint("InflateParams")
                    View layout = LayoutInflater.from(MapActivity.this).inflate(R.layout.toast_layout, null);
                    TextView Ok = layout.findViewById(R.id.okBtn);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                    TextView header = layout.findViewById(R.id.headerTv);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                    TextView description = layout.findViewById(R.id.descripTv);
                    header.setText(response.message() + " - " + response.code());
                    description.setText(getString(R.string.error_msg));
                    Ok.setOnClickListener(v -> {
                        addNewSectionDevices(sectionType, jsonObject);
                    });
                    Toast toast = new Toast(MapActivity.this);
                    toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressBarLayout.setVisibility(View.GONE);
                clearVertexNode();
                @SuppressLint("InflateParams")
                View layout = LayoutInflater.from(MapActivity.this).inflate(R.layout.toast_layout, null);
                TextView Ok = layout.findViewById(R.id.okBtn);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                TextView header = layout.findViewById(R.id.headerTv);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                TextView description = layout.findViewById(R.id.descripTv);
                header.setText(getString(R.string.error));
                description.setText(getString(R.string.error_msg));
                Ok.setOnClickListener(v -> {
                    addNewSectionDevices(sectionType, jsonObject);
                });
                Toast toast = new Toast(MapActivity.this);
                toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            }
        });
    }

    private void clearVertexNode() {
        if (selectedNode != null) {
            @SuppressLint("UseCompatLoadingForDrawables")
            Drawable drawable = getResources().getDrawable(R.drawable.dot);
            Bitmap bitmap = drawableToBitmap(drawable);
            selectedNode.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), changeBgTransparentBitmapColor(bitmap, Color.BLACK)));
            selectedNode = null;
        }

        if (vertexList != null && !vertexList.isEmpty()) {
            for (int i = 0; i < vertexList.size(); i++) {
                binding.map.getOverlays().remove(vertexList.get(i));
                binding.map.invalidate();
            }
            vertexList.clear();
        }

        coordinateList.clear();
        newSectionGeoPointList.clear();

        if (newPolyLineList != null && !newPolyLineList.isEmpty()) {
            for (int i = 0; i < newPolyLineList.size(); i++) {
                binding.map.getOverlays().remove(newPolyLineList.get(i));
                binding.map.invalidate();
            }
        }

        selectedNodeID = null;
    }

    private void CancelAnalysis() {

        if (CaPolylineList != null && !CaPolylineList.isEmpty()) {
            for (int j = 0; j < CaPolylineList.size(); j++) {
                CaPolylineList.get(j).getPaint().setColor(Color.RED);
            }
        }

        if (ohPolylineList != null && !ohPolylineList.isEmpty()) {
            for (int j = 0; j < ohPolylineList.size(); j++) {
                ohPolylineList.get(j).getPaint().setColor(Color.BLUE);
            }
        }

        if (unBalPolylineList != null && !unBalPolylineList.isEmpty()) {
            for (int j = 0; j < unBalPolylineList.size(); j++) {
                unBalPolylineList.get(j).getPaint().setColor(Color.BLACK);
            }
        }

        if (breakerList != null && !breakerList.isEmpty()) {
            for (int i = 0; i < breakerList.size(); i++) {
                @SuppressLint("UseCompatLoadingForDrawables")
                Drawable drawable = getResources().getDrawable(R.drawable.breaker);
                Bitmap bitmap = drawableToBitmap(drawable);
                breakerList.get(i).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 95)));
            }
        }

        if (transformerList != null && !transformerList.isEmpty()) {
            for (int i = 0; i < transformerList.size(); i++) {
                @SuppressLint("UseCompatLoadingForDrawables")
                Drawable drawable = getResources().getDrawable(R.drawable.transformer);
                Bitmap bitmap = drawableToBitmap(drawable);
                transformerList.get(i).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 93)));
            }
        }

        if (fuseList != null && !fuseList.isEmpty()) {
            for (int i = 0; i < fuseList.size(); i++) {
                Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "L", 90f, Color.BLACK);
                fuseList.get(i).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 0)));
            }
        }

        if (switchedList != null && !switchedList.isEmpty()) {
            for (int i = 0; i < switchedList.size(); i++) {
                Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "D", 70f, Color.BLACK);
                int paddingLeft = 0; // Left padding in pixels
                int paddingTop = 0; // Top padding in pixels
                int paddingRight = 0; // Right padding in pixels
                int paddingBottom = 12; // Bottom padding in pixels
                int newWidth = bitmap.getWidth() + paddingLeft + paddingRight;
                int newHeight = bitmap.getHeight() + paddingTop + paddingBottom;
                Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, bitmap.getConfig());
                Canvas canva = new Canvas(paddedBitmap);
                canva.drawBitmap(bitmap, paddingLeft, paddingTop, null);
                switchedList.get(i).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.BLACK), 0, 0)));
            }
        }

        if (capacitorList != null && !capacitorList.isEmpty()) {
            for (int i = 0; i < capacitorList.size(); i++) {
                capacitorList.get(i).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.BLACK), 90, 0)));
            }
        }

        if (spotLoadList != null && !spotLoadList.isEmpty()) {
            for (int i = 0; i < spotLoadList.size(); i++) {
                int paddingPx = 0;
                Bitmap paddedBitmap = Bitmap.createBitmap(BitmapImg.Spotload().getWidth() + paddingPx * 2, // Add padding to both sides
                        BitmapImg.Spotload().getHeight() + paddingPx * 2, Bitmap.Config.ARGB_8888);
                Canvas canvas1 = new Canvas(paddedBitmap);
                canvas1.drawBitmap(BitmapImg.Spotload(), paddingPx, 10, null);
                spotLoadList.get(i).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.BLACK), 0, 0)));
            }
        }

        progressBarLayout.setVisibility(View.GONE);
        binding.map.invalidate();
    }

    private void LoadFlowAnalysis(JsonObject jsonObject) {
        progressBarLayout.setProcessText("Load-Flow Analysis Run...");
        progressBarLayout.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Retrofit retrofit = RetrofitClient.getClient(MapActivity.this);
                    ApiInterface apiInterface = retrofit.create(ApiInterface.class);
                    Call<LoadFlowModel> call = apiInterface.LoadFlow(jsonObject);
                    call.enqueue(new Callback<LoadFlowModel>() {
                        @Override
                        public void onResponse(@NonNull Call<LoadFlowModel> call, @NonNull Response<LoadFlowModel> response) {
                            if (response.code() == 200) {
                                try {
                                    binding.reportBtn.setVisibility(View.GONE);
                                    progressBarLayout.setVisibility(View.GONE);
                                    LoadFlowModel loadFlowModel = response.body();
                                    assert loadFlowModel != null;
                                    if (!loadFlowModel.getOutput().getStatus().contains("Failed")) {
                                        overLoadCount = loadFlowModel.getOutput().getOverload().size();
                                        overVoltageCount = loadFlowModel.getOutput().getOverVoltage().size();
                                        underVoltageCount = loadFlowModel.getOutput().getUndervoltage().size();

                                        if (CaPolylineList != null && !CaPolylineList.isEmpty()) {
                                            for (int j = 0; j < CaPolylineList.size(); j++) {
                                                CaPolylineList.get(j).getPaint().setColor(Color.BLACK);
                                            }
                                        }

                                        if (ohPolylineList != null && !ohPolylineList.isEmpty()) {
                                            for (int j = 0; j < ohPolylineList.size(); j++) {
                                                ohPolylineList.get(j).getPaint().setColor(Color.BLACK);
                                            }
                                        }

                                        if (unBalPolylineList != null && !unBalPolylineList.isEmpty()) {
                                            for (int j = 0; j < unBalPolylineList.size(); j++) {
                                                unBalPolylineList.get(j).getPaint().setColor(Color.BLACK);
                                            }
                                        }
                                        new LoadFlowAnalysis(response.body()).execute();
                                    } else {
                                        Snackbar.make(binding.getRoot(), getString(R.string.error_msg), Snackbar.LENGTH_LONG).show();
                                    }
                                } catch (Exception e) {
                                    Config.isLoadFlow = false;
                                    binding.reportBtn.setVisibility(View.GONE);
                                    progressBarLayout.setVisibility(View.GONE);
                                    Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
                                }
                            } else if (response.code() == 401) {
                                Config.isLoadFlow = false;
                                binding.reportBtn.setVisibility(View.GONE);
                                progressBarLayout.setVisibility(View.GONE);
                                prefManager.setIsUserLogin(false);
                                startActivity(new Intent(MapActivity.this, LoginActivity.class));
                                finish();
                            } else {
                                Config.isLoadFlow = false;
                                binding.reportBtn.setVisibility(View.GONE);
                                progressBarLayout.setVisibility(View.GONE);
                                Snackbar snack = Snackbar.make(findViewById(android.R.id.content), response.message() + " - " + response.code(), Snackbar.LENGTH_SHORT);
                                snack.show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<LoadFlowModel> call, @NonNull Throwable t) {
                            Config.isLoadFlow = false;
                            binding.reportBtn.setVisibility(View.GONE);
                            progressBarLayout.setVisibility(View.GONE);
                            Snackbar snack = Snackbar.make(findViewById(android.R.id.content), getString(R.string.error_msg), Snackbar.LENGTH_SHORT);
                            snack.show();
                        }
                    });
                } catch (Exception e) {
                    progressBarLayout.setVisibility(View.GONE);
                    Log.d("Exception", e.getLocalizedMessage());
                }
            }
        }).start();
    }

    @SuppressLint("SetTextI18n")
    private void ShortCircuitAnalysis(JsonObject jsonObject) {
        progressBarLayout.setProcessText("Short Circuit Analysis Run..");
        progressBarLayout.setVisibility(View.VISIBLE);
        Retrofit retrofit = RetrofitClient.getClient(this);
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<ShortCircuitModel> call = apiInterface.ShortCircuit(jsonObject);
        call.enqueue(new Callback<ShortCircuitModel>() {
            @Override
            public void onResponse(@NonNull Call<ShortCircuitModel> call, @NonNull Response<ShortCircuitModel> response) {
                if (response.code() == 200) {
                    try {
                        progressBarLayout.setVisibility(View.GONE);
                        ShortCircuitModel shortCircuitModel = response.body();
                        assert shortCircuitModel != null;
                        if (!shortCircuitModel.getOutput().getStatus().contains("Failed")) {
                            overLoadCount = shortCircuitModel.getOutput().getOverload().size();
                            overVoltageCount = shortCircuitModel.getOutput().getOverVoltage().size();
                            underVoltageCount = shortCircuitModel.getOutput().getUndervoltage().size();
                            ratingCount = shortCircuitModel.getOutput().getShortCircuitRating().size();

                            if (CaPolylineList != null && !CaPolylineList.isEmpty()) {
                                for (int j = 0; j < CaPolylineList.size(); j++) {
                                    CaPolylineList.get(j).getPaint().setColor(Color.BLACK);
                                }
                            }

                            if (ohPolylineList != null && !ohPolylineList.isEmpty()) {
                                for (int j = 0; j < ohPolylineList.size(); j++) {
                                    ohPolylineList.get(j).getPaint().setColor(Color.BLACK);
                                }
                            }

                            if (unBalPolylineList != null && !unBalPolylineList.isEmpty()) {
                                for (int j = 0; j < unBalPolylineList.size(); j++) {
                                    unBalPolylineList.get(j).getPaint().setColor(Color.BLACK);
                                }
                            }

                            new ShortCircuitAnalysis(response.body()).execute();
                        } else {
                            Snackbar.make(binding.getRoot(), getString(R.string.error_msg), Snackbar.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        progressBarLayout.setVisibility(View.GONE);
                        Snackbar snack = Snackbar.make(findViewById(android.R.id.content), getString(R.string.error_msg), Snackbar.LENGTH_LONG);
                        snack.show();
                        Log.d("Exception", e.getLocalizedMessage());
                    }
                } else if (response.code() == 401) {
                    prefManager.setIsUserLogin(false);
                    startActivity(new Intent(MapActivity.this, LoginActivity.class));
                    finish();
                } else {
                    progressBarLayout.setVisibility(View.GONE);
                    Snackbar snack = Snackbar.make(findViewById(android.R.id.content), response.message() + " - " + response.code(), Snackbar.LENGTH_LONG);
                    snack.show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ShortCircuitModel> call, @NonNull Throwable t) {
                progressBarLayout.setVisibility(View.GONE);
                Snackbar snack = Snackbar.make(findViewById(android.R.id.content), getString(R.string.error_msg), Snackbar.LENGTH_LONG);
                snack.show();
            }
        });
    }

    private void LoadAllocationAnalysis(JsonObject jsonObject) {
        Retrofit retrofit = RetrofitClient.getClient(MapActivity.this);
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<LoadAllocationModel> call = apiInterface.LoadAllocation(jsonObject);
        call.enqueue(new Callback<LoadAllocationModel>() {
            @Override
            public void onResponse(@NonNull Call<LoadAllocationModel> call, @NonNull Response<LoadAllocationModel> response) {
                if (response.code() == 200) {
                    if (CaPolylineList != null && !CaPolylineList.isEmpty()) {
                        for (int j = 0; j < CaPolylineList.size(); j++) {
                            CaPolylineList.get(j).getPaint().setColor(Color.BLACK);
                        }
                    }

                    if (ohPolylineList != null && !ohPolylineList.isEmpty()) {
                        for (int j = 0; j < ohPolylineList.size(); j++) {
                            ohPolylineList.get(j).getPaint().setColor(Color.BLACK);
                        }
                    }

                    if (unBalPolylineList != null && !unBalPolylineList.isEmpty()) {
                        for (int j = 0; j < unBalPolylineList.size(); j++) {
                            unBalPolylineList.get(j).getPaint().setColor(Color.BLACK);
                        }
                    }
                    new LoadAllocationAnalysis(response.body()).execute();

                } else if (response.code() == 401) {
                    prefManager.setIsUserLogin(false);
                    startActivity(new Intent(MapActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Snackbar snack = Snackbar.make(findViewById(android.R.id.content), response.message() + " - " + response.code(), Snackbar.LENGTH_LONG);
                    snack.show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoadAllocationModel> call, @NonNull Throwable t) {
                Snackbar snack = Snackbar.make(findViewById(android.R.id.content), getString(R.string.error_msg), Snackbar.LENGTH_LONG);
                snack.show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void getUpTracingData(String up) {
        if (networkId != null && nodeId != null) {
            progressBarLayout.setProcessText("Tracing UpStreams...");
            progressBarLayout.setVisibility(View.VISIBLE);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("NetworkId", networkId);
            jsonObject.addProperty("Nodeid", nodeId);
            jsonObject.addProperty("Direction", "Upstream");
            jsonObject.addProperty("UserType", prefManager.getUserType());
            jsonObject.addProperty("CYMDBNET", prefManager.getDBName());
            Retrofit retrofit = RetrofitClient.getClient(MapActivity.this);
            ApiInterface apiInterface = retrofit.create(ApiInterface.class);
            Call<Tracing> call = apiInterface.getTracingData(jsonObject);
            call.enqueue(new Callback<Tracing>() {
                @Override
                public void onResponse(@NonNull Call<Tracing> call, @NonNull Response<Tracing> response) {
                    if (response.code() == 200) {
                        try {
                            progressBarLayout.setVisibility(View.GONE);
                            new TraceDevices(response.body(), up).execute();
                        } catch (Exception e) {
                            Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
                        }
                    } else if (response.code() == 401) {
                        prefManager.setIsUserLogin(false);
                        startActivity(new Intent(MapActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        progressBarLayout.setVisibility(View.GONE);
                        Snackbar snack = Snackbar.make(findViewById(android.R.id.content), response.message() + " - " + response.code(), Snackbar.LENGTH_SHORT);
                        snack.show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Tracing> call, @NonNull Throwable t) {
                    progressBarLayout.setVisibility(View.GONE);
                    Snackbar snack = Snackbar.make(findViewById(android.R.id.content), getString(R.string.error_msg), Snackbar.LENGTH_SHORT);
                    snack.show();
                }
            });
        } else {
            Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Please select any network!", Snackbar.LENGTH_LONG);
            snack.show();
        }
    }

    @SuppressLint("SetTextI18n")
    private void getDownTracingData(String type) {
        if (networkId != null && nodeId != null) {
            progressBarLayout.setVisibility(View.VISIBLE);
            progressBarLayout.setProcessText("Tracing DownStreams...");
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("NetworkId", networkId);
            jsonObject.addProperty("Nodeid", nodeId);
            jsonObject.addProperty("Direction", "Downstream");
            jsonObject.addProperty("UserType", prefManager.getUserType());
            jsonObject.addProperty("CYMDBNET", prefManager.getDBName());
            Retrofit retrofit = RetrofitClient.getClient(MapActivity.this);
            ApiInterface apiInterface = retrofit.create(ApiInterface.class);
            Call<Tracing> call = apiInterface.getTracingData(jsonObject);
            call.enqueue(new Callback<Tracing>() {
                @Override
                public void onResponse(@NonNull Call<Tracing> call, @NonNull Response<Tracing> response) {
                    if (response.code() == 200) {
                        try {
                            progressBarLayout.setVisibility(View.GONE);
                            new TraceDevices(response.body(), type).execute();
                        } catch (Exception e) {
                            Log.d("Exception", e.getLocalizedMessage());
                        }
                    } else if (response.code() == 401) {
                        prefManager.setIsUserLogin(false);
                        startActivity(new Intent(MapActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        progressBarLayout.setVisibility(View.GONE);
                        Snackbar snack = Snackbar.make(findViewById(android.R.id.content), response.message() + " - " + response.code(), Snackbar.LENGTH_SHORT);
                        snack.show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Tracing> call, @NonNull Throwable t) {
                    progressBarLayout.setVisibility(View.GONE);
                    Snackbar snack = Snackbar.make(findViewById(android.R.id.content), getString(R.string.error_msg), Snackbar.LENGTH_SHORT);
                    snack.show();
                }
            });
        } else {
            Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Please select any network!", Snackbar.LENGTH_LONG);
            snack.show();
        }
    }

    private void getTopology(JsonArray jsonArray) {
        Retrofit retrofit = RetrofitClient.getClient(MapActivity.this);
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("NetworkId", jsonArray);
        jsonObject.addProperty("UserType", prefManager.getUserType());
        jsonObject.addProperty("CYMDBNET", prefManager.getDBName());
        Call<Topology> call = apiInterface.getTopologyData(jsonObject);
        call.enqueue(new Callback<Topology>() {
            @Override
            public void onResponse(@NonNull Call<Topology> call, @NonNull Response<Topology> response) {
                if (response.code() == 200) {
                    try {
                        isTopology = true;
                        Topology topology = response.body();
                        assert topology != null;
                        if (topology.getOutput().getDisconnected() != null) {
                            topology.getOutput().getDisconnected().size();
                            ArrayList<DType> list = new ArrayList<>();
                            DType type;
                            for (int i = 0; i < topology.getOutput().getDisconnected().size(); i++) {
                                type = new DType(topology.getOutput().getDisconnected().get(i), 40);
                                list.add(type);
                            }
                            Continent continent = new Continent("disconnected" + " " + "(" + list.size() + ")", list);
                            continentList.add(continent);
                        }

                        if (topology.getOutput().getInterconnection() != null) {
                            topology.getOutput().getInterconnection().size();
                            ArrayList<DType> list = new ArrayList<>();
                            DType type;
                            for (int i = 0; i < topology.getOutput().getInterconnection().size(); i++) {
                                type = new DType(topology.getOutput().getInterconnection().get(i), 41);
                                list.add(type);
                            }
                            Continent continent = new Continent("Interconnection" + " " + "(" + list.size() + ")", list);
                            continentList.add(continent);
                        }

                        if (topology.getOutput().getIsolated() != null) {
                            topology.getOutput().getIsolated().size();
                            ArrayList<DType> list = new ArrayList<>();
                            DType type;
                            for (int i = 0; i < topology.getOutput().getIsolated().size(); i++) {
                                type = new DType(topology.getOutput().getIsolated().get(i), 40);
                                list.add(type);
                            }
                            Continent continent = new Continent("Isolated" + " " + "(" + list.size() + ")", list);
                            continentList.add(continent);
                        }

                        if (topology.getOutput().getLoop() != null) {
                            topology.getOutput().getLoop().size();
                            ArrayList<DType> list = new ArrayList<>();
                            DType type;
                            for (int i = 0; i < topology.getOutput().getLoop().size(); i++) {
                                type = new DType(topology.getOutput().getLoop().get(i), 41);
                                list.add(type);
                            }
                            Continent continent = new Continent("Loop" + " " + "(" + list.size() + ")", list);
                            continentList.add(continent);
                        }

                        if (topology.getOutput().getSourcenode() != null) {
                            topology.getOutput().getSourcenode().size();
                            ArrayList<DType> list = new ArrayList<>();
                            DType type;
                            for (int i = 0; i < topology.getOutput().getSourcenode().size(); i++) {
                                type = new DType(topology.getOutput().getSourcenode().get(i), 41);
                                list.add(type);
                            }
                            Continent continent = new Continent("source Node" + " " + "(" + list.size() + ")", list);
                            continentList.add(continent);
                        }
                        adapter = new ExpandableDeviceAdapter(MapActivity.this, continentList);
                        binding.navigationmenu.setAdapter(adapter);
                    } catch (Exception e) {
                        Log.d("Exception", e.getLocalizedMessage());
                    }
                } else if (response.code() == 401) {
                    prefManager.setIsUserLogin(false);
                    startActivity(new Intent(MapActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Snackbar snack = Snackbar.make(findViewById(android.R.id.content), response.message() + " - " + response.code(), Snackbar.LENGTH_LONG);
                    snack.show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Topology> call, @NonNull Throwable t) {
                Snackbar snack = Snackbar.make(findViewById(android.R.id.content), getString(R.string.error_msg), Snackbar.LENGTH_LONG);
                snack.show();
            }
        });
    }

    public void getDevices(String deviceNumber, String num) {
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        progressBarLayout.setProcessText("Device Zoom To Center...");
        progressBarLayout.setVisibility(View.VISIBLE);
        if (num.equalsIgnoreCase("1")) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            try {
                progressBarLayout.setVisibility(View.GONE);
                int layerSize = CaObject.getJSONArray("features").length();
                for (int i = 0; i < layerSize; i++) {
                    if (CaObject.getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceNumber").equalsIgnoreCase(deviceNumber)) {
                        int coordinateSize = CaObject.getJSONArray("features").getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates").length();
                        List<GeoPoint> geoPointList = new ArrayList<>();

                        for (int j = 0; j < coordinateSize; j++) {
                            geoPointList.add(new GeoPoint(CaObject.getJSONArray("features").getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(j).getDouble(1), CaObject.getJSONArray("features").getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(j).getDouble(0)));
                        }

                        BoundingBox boundingBox1 = BoundingBox.fromGeoPoints(geoPointList);
                        binding.map.zoomToBoundingBox(boundingBox1, true);
                        binding.map.getBoundingBox().getCenter();
                        binding.map.getController().setCenter(boundingBox1.getCenter());
                        binding.map.zoomToBoundingBox(boundingBox1.increaseByScale(1.5f), true);
                        binding.map.invalidate();
                        geoPointList.clear();
                    }
                }
                if (CaSectionId.get(deviceNumber) != null) {
                    if (Config.isLoadFlow) {
                        highlightLoadFlowSection(CaSectionId.get(deviceNumber), deviceNumber, 0);
                    } else if (Config.isShortCircuit) {
                        highlightShortCircuitSection(CaSectionId.get(deviceNumber), deviceNumber, 0);
                    } else {
                        highlightSection(CaSectionId.get(deviceNumber), "1", 0);
                    }
                }
            } catch (Exception e) {
                Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
            }
        } else if (num.equalsIgnoreCase("2")) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            try {
                progressBarLayout.setVisibility(View.GONE);
                int layerSize = OhObject.getJSONArray("features").length();
                for (int i = 0; i < layerSize; i++) {
                    if (OhObject.getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceNumber").equalsIgnoreCase(deviceNumber)) {
                        int coordinateSize = OhObject.getJSONArray("features").getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates").length();
                        List<GeoPoint> geoPointList = new ArrayList<>();

                        for (int j = 0; j < coordinateSize; j++) {
                            geoPointList.add(new GeoPoint(OhObject.getJSONArray("features").getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(j).getDouble(1), OhObject.getJSONArray("features").getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(j).getDouble(0)));
                        }

                        BoundingBox boundingBox1 = BoundingBox.fromGeoPoints(geoPointList);
                        binding.map.zoomToBoundingBox(boundingBox1, true);
                        binding.map.getBoundingBox().getCenter();
                        binding.map.getController().setCenter(boundingBox1.getCenter());
                        binding.map.zoomToBoundingBox(boundingBox1.increaseByScale(1.5f), true);
                        binding.map.invalidate();
                        geoPointList.clear();

                        if (OhSectionId.get(deviceNumber) != null) {
                            if (Config.isLoadFlow) {
                                highlightLoadFlowSection(OhSectionId.get(deviceNumber), deviceNumber, 0);
                            } else if (Config.isShortCircuit) {
                                highlightShortCircuitSection(OhSectionId.get(deviceNumber), deviceNumber, 0);
                            } else {
                                highlightSection(OhSectionId.get(deviceNumber), "2", 0);
                            }
                        }

                    }
                }
            } catch (Exception e) {
                Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
            }
        } else if (num.equalsIgnoreCase("23")) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            try {
                progressBarLayout.setVisibility(View.GONE);
                int layerSize = UnBalObject.getJSONArray("features").length();
                for (int i = 0; i < layerSize; i++) {
                    if (UnBalObject.getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceNumber").equalsIgnoreCase(deviceNumber)) {
                        int coordinateSize = UnBalObject.getJSONArray("features").getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates").length();
                        List<GeoPoint> geoPointList = new ArrayList<>();

                        for (int j = 0; j < coordinateSize; j++) {
                            geoPointList.add(new GeoPoint(UnBalObject.getJSONArray("features").getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(j).getDouble(1), UnBalObject.getJSONArray("features").getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(j).getDouble(0)));
                        }

                        BoundingBox boundingBox1 = BoundingBox.fromGeoPoints(geoPointList);
                        binding.map.zoomToBoundingBox(boundingBox1, true);
                        binding.map.getBoundingBox().getCenter();
                        binding.map.getController().setCenter(boundingBox1.getCenter());
                        binding.map.zoomToBoundingBox(boundingBox1.increaseByScale(1.5f), true);
                        binding.map.invalidate();
                        geoPointList.clear();

                        if (UnBalSectionId.get(deviceNumber) != null) {
                            if (Config.isLoadFlow) {
                                highlightLoadFlowSection(UnBalSectionId.get(deviceNumber), deviceNumber, 0);
                            } else if (Config.isShortCircuit) {
                                highlightShortCircuitSection(UnBalSectionId.get(deviceNumber), deviceNumber, 0);
                            } else {
                                highlightSection(UnBalSectionId.get(deviceNumber), "2", 0);
                            }
                        }

                    }
                }
            } catch (Exception e) {
                Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
            }
        } else {
            progressBarLayout.setVisibility(View.GONE);
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            getDeviceLocation(deviceNumber, num);
        }
    }

    private void getDeviceLocation(String deviceNumber, String deviceType) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("Id", deviceNumber);
        jsonObject.addProperty("DeviceType", deviceType);
        jsonObject.addProperty("CYMDBNET", prefManager.getDBName());
        Retrofit retrofit = RetrofitClient.getClient(MapActivity.this);
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<ZoomToLayer> call = apiInterface.netWorkZoomToLayer(jsonObject);
        call.enqueue(new Callback<ZoomToLayer>() {
            @Override
            public void onResponse(@NonNull Call<ZoomToLayer> call, @NonNull Response<ZoomToLayer> response) {
                if (response.code() == 200) {
                    try {
                        ZoomToLayer zoomToLayer = response.body();
                        assert zoomToLayer != null;
                        if (!zoomToLayer.getOutput().contains("[]") && !zoomToLayer.getOutput().isEmpty()) {
                            if (zoomToLayer.getOutput().get(0).getY() != null && zoomToLayer.getOutput().get(0).getX() != null) {
                                Double x = Double.valueOf(zoomToLayer.getOutput().get(0).getY());
                                Double y = Double.valueOf(zoomToLayer.getOutput().get(0).getX());
                                if (x != 0 && y != 0) {
                                    if (zoomToLayer.getOutput().get(0).getDeviceType() != null && !zoomToLayer.getOutput().get(0).getDeviceType().isEmpty() && zoomToLayer.getOutput().get(0).getDeviceNumber() != null && !zoomToLayer.getOutput().get(0).getDeviceNumber().isEmpty()) {
                                        if (Config.isLoadFlow) {
                                            if (zoomToLayer.getOutput().get(0).getDeviceType().contains("8")) {
                                                if (breakerSectionId.get(zoomToLayer.getOutput().get(0).getDeviceNumber()) != null) {
                                                    highlightLoadFlowDevice(breakerSectionId.get(zoomToLayer.getOutput().get(0).getDeviceNumber()), zoomToLayer.getOutput().get(0).getDeviceType(), zoomToLayer.getOutput().get(0).getSectionId(), Integer.parseInt(zoomToLayer.getOutput().get(0).getLocation()));
                                                }
                                            } else if (zoomToLayer.getOutput().get(0).getDeviceType().contains("5")) {
                                                if (transformerSectionId.get(zoomToLayer.getOutput().get(0).getDeviceNumber()) != null) {
                                                    highlightLoadFlowDevice(transformerSectionId.get(zoomToLayer.getOutput().get(0).getDeviceNumber()), zoomToLayer.getOutput().get(0).getDeviceType(), zoomToLayer.getOutput().get(0).getSectionId(), Integer.parseInt(zoomToLayer.getOutput().get(0).getLocation()));
                                                }
                                            } else if (zoomToLayer.getOutput().get(0).getDeviceType().contains("14")) {
                                                if (fuseSectionId.get(zoomToLayer.getOutput().get(0).getDeviceNumber()) != null) {
                                                    highlightLoadFlowDevice(fuseSectionId.get(zoomToLayer.getOutput().get(0).getDeviceNumber()), zoomToLayer.getOutput().get(0).getDeviceType(), zoomToLayer.getOutput().get(0).getSectionId(), Integer.parseInt(zoomToLayer.getOutput().get(0).getLocation()));
                                                }
                                            } else if (zoomToLayer.getOutput().get(0).getDeviceType().contains("13")) {
                                                if (switchSectionId.get(zoomToLayer.getOutput().get(0).getDeviceNumber()) != null) {
                                                    highlightLoadFlowDevice(switchSectionId.get(zoomToLayer.getOutput().get(0).getDeviceNumber()), zoomToLayer.getOutput().get(0).getDeviceType(), zoomToLayer.getOutput().get(0).getSectionId(), Integer.parseInt(zoomToLayer.getOutput().get(0).getLocation()));
                                                }
                                            } else if (zoomToLayer.getOutput().get(0).getDeviceType().contains("61")) {
                                                if (capacitorSectionId.get(zoomToLayer.getOutput().get(0).getDeviceNumber()) != null) {
                                                    highlightLoadFlowDevice(capacitorSectionId.get(zoomToLayer.getOutput().get(0).getDeviceNumber()), zoomToLayer.getOutput().get(0).getDeviceType(), zoomToLayer.getOutput().get(0).getSectionId(), Integer.parseInt(zoomToLayer.getOutput().get(0).getLocation()));
                                                }
                                            } else if (zoomToLayer.getOutput().get(0).getDeviceType().contains("20")) {
                                                if (spotLoadSectionId.get(zoomToLayer.getOutput().get(0).getDeviceNumber()) != null) {
                                                    highlightLoadFlowDevice(spotLoadSectionId.get(zoomToLayer.getOutput().get(0).getDeviceNumber()), zoomToLayer.getOutput().get(0).getDeviceType(), zoomToLayer.getOutput().get(0).getSectionId(), Integer.parseInt(zoomToLayer.getOutput().get(0).getLocation()));
                                                }
                                            } else {
                                                GeoPoint geoPoint = new GeoPoint(x, y);
                                                binding.map.getController().animateTo(geoPoint, 27.0, 0L);
                                                binding.map.getController().setCenter(geoPoint);
                                                binding.map.invalidate();
                                            }
                                        } else if (Config.isShortCircuit) {
                                            if (zoomToLayer.getOutput().get(0).getDeviceType().contains("8")) {
                                                if (breakerSectionId.get(zoomToLayer.getOutput().get(0).getDeviceNumber()) != null) {
                                                    highlightShortCircuitDevice(breakerSectionId.get(zoomToLayer.getOutput().get(0).getDeviceNumber()), zoomToLayer.getOutput().get(0).getDeviceType(), zoomToLayer.getOutput().get(0).getSectionId(), Integer.parseInt(zoomToLayer.getOutput().get(0).getLocation()));
                                                }
                                            } else if (zoomToLayer.getOutput().get(0).getDeviceType().contains("5")) {
                                                if (transformerSectionId.get(zoomToLayer.getOutput().get(0).getDeviceNumber()) != null) {
                                                    highlightShortCircuitDevice(transformerSectionId.get(zoomToLayer.getOutput().get(0).getDeviceNumber()), zoomToLayer.getOutput().get(0).getDeviceType(), zoomToLayer.getOutput().get(0).getSectionId(), Integer.parseInt(zoomToLayer.getOutput().get(0).getLocation()));
                                                }
                                            } else if (zoomToLayer.getOutput().get(0).getDeviceType().contains("14")) {
                                                if (fuseSectionId.get(zoomToLayer.getOutput().get(0).getDeviceNumber()) != null) {
                                                    highlightShortCircuitDevice(fuseSectionId.get(zoomToLayer.getOutput().get(0).getDeviceNumber()), zoomToLayer.getOutput().get(0).getDeviceType(), zoomToLayer.getOutput().get(0).getSectionId(), Integer.parseInt(zoomToLayer.getOutput().get(0).getLocation()));
                                                }
                                            } else if (zoomToLayer.getOutput().get(0).getDeviceType().contains("13")) {
                                                if (switchSectionId.get(zoomToLayer.getOutput().get(0).getDeviceNumber()) != null) {
                                                    highlightShortCircuitDevice(switchSectionId.get(zoomToLayer.getOutput().get(0).getDeviceNumber()), zoomToLayer.getOutput().get(0).getDeviceType(), zoomToLayer.getOutput().get(0).getSectionId(), Integer.parseInt(zoomToLayer.getOutput().get(0).getLocation()));
                                                }
                                            } else if (zoomToLayer.getOutput().get(0).getDeviceType().contains("61")) {
                                                if (capacitorSectionId.get(zoomToLayer.getOutput().get(0).getDeviceNumber()) != null) {
                                                    highlightShortCircuitDevice(capacitorSectionId.get(zoomToLayer.getOutput().get(0).getDeviceNumber()), zoomToLayer.getOutput().get(0).getDeviceType(), zoomToLayer.getOutput().get(0).getSectionId(), Integer.parseInt(zoomToLayer.getOutput().get(0).getLocation()));
                                                }
                                            } else if (zoomToLayer.getOutput().get(0).getDeviceType().contains("20")) {
                                                if (spotLoadSectionId.get(zoomToLayer.getOutput().get(0).getDeviceNumber()) != null) {
                                                    highlightShortCircuitDevice(spotLoadSectionId.get(zoomToLayer.getOutput().get(0).getDeviceNumber()), zoomToLayer.getOutput().get(0).getDeviceType(), zoomToLayer.getOutput().get(0).getSectionId(), Integer.parseInt(zoomToLayer.getOutput().get(0).getLocation()));
                                                }
                                            } else {
                                                GeoPoint geoPoint = new GeoPoint(x, y);
                                                binding.map.getController().animateTo(geoPoint, 27.0, 0L);
                                                binding.map.getController().setCenter(geoPoint);
                                                binding.map.invalidate();
                                            }
                                        } else {
                                            if (zoomToLayer.getOutput().get(0).getDeviceType().contains("8")) {
                                                if (breakerSectionId.get(zoomToLayer.getOutput().get(0).getDeviceNumber()) != null) {
                                                    highlightDevice(breakerSectionId.get(zoomToLayer.getOutput().get(0).getDeviceNumber()), zoomToLayer.getOutput().get(0).getDeviceType(), zoomToLayer.getOutput().get(0).getDeviceNumber(), zoomToLayer.getOutput().get(0).getSectionId(), Integer.parseInt(zoomToLayer.getOutput().get(0).getLocation()));
                                                }
                                            } else if (zoomToLayer.getOutput().get(0).getDeviceType().contains("5")) {
                                                if (transformerSectionId.get(zoomToLayer.getOutput().get(0).getDeviceNumber()) != null) {
                                                    highlightDevice(transformerSectionId.get(zoomToLayer.getOutput().get(0).getDeviceNumber()), zoomToLayer.getOutput().get(0).getDeviceType(), zoomToLayer.getOutput().get(0).getDeviceNumber(), zoomToLayer.getOutput().get(0).getSectionId(), Integer.parseInt(zoomToLayer.getOutput().get(0).getLocation()));
                                                }
                                            } else if (zoomToLayer.getOutput().get(0).getDeviceType().contains("14")) {
                                                if (fuseSectionId.get(zoomToLayer.getOutput().get(0).getDeviceNumber()) != null) {
                                                    highlightDevice(fuseSectionId.get(zoomToLayer.getOutput().get(0).getDeviceNumber()), zoomToLayer.getOutput().get(0).getDeviceType(), zoomToLayer.getOutput().get(0).getDeviceNumber(), zoomToLayer.getOutput().get(0).getSectionId(), Integer.parseInt(zoomToLayer.getOutput().get(0).getLocation()));
                                                }
                                            } else if (zoomToLayer.getOutput().get(0).getDeviceType().contains("13")) {
                                                if (switchSectionId.get(zoomToLayer.getOutput().get(0).getDeviceNumber()) != null) {
                                                    highlightDevice(switchSectionId.get(zoomToLayer.getOutput().get(0).getDeviceNumber()), zoomToLayer.getOutput().get(0).getDeviceType(), zoomToLayer.getOutput().get(0).getDeviceNumber(), zoomToLayer.getOutput().get(0).getSectionId(), Integer.parseInt(zoomToLayer.getOutput().get(0).getLocation()));
                                                }
                                            } else if (zoomToLayer.getOutput().get(0).getDeviceType().contains("61")) {
                                                if (capacitorSectionId.get(zoomToLayer.getOutput().get(0).getDeviceNumber()) != null) {
                                                    highlightDevice(capacitorSectionId.get(zoomToLayer.getOutput().get(0).getDeviceNumber()), zoomToLayer.getOutput().get(0).getDeviceType(), zoomToLayer.getOutput().get(0).getDeviceNumber(), zoomToLayer.getOutput().get(0).getSectionId(), Integer.parseInt(zoomToLayer.getOutput().get(0).getLocation()));
                                                }
                                            } else if (zoomToLayer.getOutput().get(0).getDeviceType().contains("20")) {
                                                if (spotLoadSectionId.get(zoomToLayer.getOutput().get(0).getDeviceNumber()) != null) {
                                                    highlightDevice(spotLoadSectionId.get(zoomToLayer.getOutput().get(0).getDeviceNumber()), zoomToLayer.getOutput().get(0).getDeviceType(), zoomToLayer.getOutput().get(0).getDeviceNumber(), zoomToLayer.getOutput().get(0).getSectionId(), Integer.parseInt(zoomToLayer.getOutput().get(0).getLocation()));
                                                }
                                            } else {
                                                GeoPoint geoPoint = new GeoPoint(x, y);
                                                binding.map.getController().animateTo(geoPoint, 27.0, 0L);
                                                binding.map.getController().setCenter(geoPoint);
                                                binding.map.invalidate();
                                            }
                                        }
                                    }
                                    GeoPoint geoPoint = new GeoPoint(x, y);
                                    binding.map.getController().animateTo(geoPoint, 27.0, 0L);
                                    binding.map.getController().setCenter(geoPoint);
                                    binding.map.invalidate();
                                }
                            } else {
                                Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Not Found LatLon!", Snackbar.LENGTH_LONG);
                                snack.show();
                            }
                        }
                    } catch (Exception e) {
                        Log.d("Exception", e.getLocalizedMessage());
                    }
                } else if (response.code() == 401) {
                    prefManager.setIsUserLogin(false);
                    startActivity(new Intent(MapActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Snackbar snack = Snackbar.make(findViewById(android.R.id.content), response.message() + " - " + response.code(), Snackbar.LENGTH_LONG);
                    snack.show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ZoomToLayer> call, @NonNull Throwable t) {
                Snackbar snack = Snackbar.make(findViewById(android.R.id.content), getString(R.string.error_msg), Snackbar.LENGTH_LONG);
                snack.show();
            }
        });

    }

    private void expandAll() {
        int count = adapter.getGroupCount();
        if (binding.navigationmenu.getExpandableListAdapter() != null) {
            for (int i = 0; i < count; i++) {
                binding.navigationmenu.expandGroup(i);
            }
        }
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            int result;
            List<String> listPermissionsNeeded = new ArrayList<>();
            for (String p : permissions1) {
                result = ContextCompat.checkSelfPermission(MapActivity.this, p);
                if (result != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(p);
                }
            }
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
                return false;
            }

        } else {
            int result;
            List<String> listPermissionsNeeded = new ArrayList<>();
            for (String p : permissions) {
                result = ContextCompat.checkSelfPermission(MapActivity.this, p);
                if (result != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(p);
                }
            }

            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
                return false;
            }

        }
        return true;
        /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            int result;
            List<String> listPermissionsNeeded = new ArrayList<>();
            for (String p : permissions1) {
                result = ContextCompat.checkSelfPermission(MapActivity.this, p);
                if (result != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(p);
                }
            }
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
                return false;
            }

        } else {
            int result;
            List<String> listPermissionsNeeded = new ArrayList<>();
            for (String p : permissions) {
                result = ContextCompat.checkSelfPermission(MapActivity.this, p);
                if (result != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(p);
                }
            }

            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
                return false;
            }

        }
        return true;*/
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MULTIPLE_PERMISSIONS);
        } else {
            if (locationOver != null) {
                if (Objects.requireNonNull(intent.getStringExtra("Type")).contains("AddNetwork") && !isSource) {
                    SourceEditDialog sourceEditDialog = new SourceEditDialog(this, getSupportFragmentManager(), getLifecycle(), locationOver.getLatitude(), locationOver.getLongitude());
                    sourceEditDialog.setCancelable(false);
                    sourceEditDialog.show();
                } else {
                    newSectionGeoPointList.add(new GeoPoint(locationOver.getLatitude(), locationOver.getLongitude()));
                    coordinateList.add(new GeoPoint(locationOver.getLatitude(), locationOver.getLongitude()));
                    addNewSections(coordinateList);
                }
            }
        }
    }

    private void reSetColor() {
        if (Config.isLoadFlow) {
            isTracing = false;
            if (CaPolylineList != null && !CaPolylineList.isEmpty()) {
                for (int j = 0; j < CaPolylineList.size(); j++) {
                    CaPolylineList.get(j).getPaint().setColor(Color.BLACK);
                }
            }

            if (ohPolylineList != null && !ohPolylineList.isEmpty()) {
                for (int j = 0; j < ohPolylineList.size(); j++) {
                    ohPolylineList.get(j).getPaint().setColor(Color.BLACK);
                }
            }

            if (unBalPolylineList != null && !unBalPolylineList.isEmpty()) {
                for (int j = 0; j < unBalPolylineList.size(); j++) {
                    unBalPolylineList.get(j).getPaint().setColor(Color.BLACK);
                }
            }

            if (secNodeList != null && !secNodeList.isEmpty()) {
                for (int j = 0; j < secNodeList.size(); j++) {
                    secNodeList.get(j).getPaint().setColor(Color.BLACK);
                }
            }

            if (breakerList != null && !breakerList.isEmpty()) {
                for (int i = 0; i < breakerList.size(); i++) {
                    @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.breaker);
                    Bitmap bitmap = drawableToBitmap(drawable);
                    breakerList.get(i).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 95)));
                }
            }

            if (transformerList != null && !transformerList.isEmpty()) {
                for (int i = 0; i < transformerList.size(); i++) {
                    @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.transformer);
                    Bitmap bitmap = drawableToBitmap(drawable);
                    transformerList.get(i).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 93)));
                }
            }

            if (fuseList != null && !fuseList.isEmpty()) {
                for (int i = 0; i < fuseList.size(); i++) {
                    Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "L", 90f, Color.BLACK);
                    fuseList.get(i).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 0)));
                }
            }

            if (switchedList != null && !switchedList.isEmpty()) {
                for (int i = 0; i < switchedList.size(); i++) {
                    Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "D", 70f, Color.BLACK);
                    int paddingLeft = 0;
                    int paddingTop = 0; // Top padding in pixels
                    int paddingRight = 0; // Right padding in pixels
                    int paddingBottom = 12; // Bottom padding in pixels
                    int newWidth = bitmap.getWidth() + paddingLeft + paddingRight;
                    int newHeight = bitmap.getHeight() + paddingTop + paddingBottom;
                    Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, bitmap.getConfig());
                    Canvas canva = new Canvas(paddedBitmap);
                    canva.drawBitmap(bitmap, paddingLeft, paddingTop, null);
                    switchedList.get(i).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.BLACK), 0, 0)));
                }
            }

            if (capacitorList != null && !capacitorList.isEmpty()) {
                for (int i = 0; i < capacitorList.size(); i++) {
                    capacitorList.get(i).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.BLACK), 90, 0)));
                }
            }

            if (spotLoadList != null && !spotLoadList.isEmpty()) {
                for (int i = 0; i < spotLoadList.size(); i++) {
                    int paddingPx = 0;
                    Bitmap paddedBitmap = Bitmap.createBitmap(BitmapImg.Spotload().getWidth() + paddingPx * 2, // Add padding to both sides
                            BitmapImg.Spotload().getHeight() + paddingPx * 2, Bitmap.Config.ARGB_8888);
                    Canvas canvas1 = new Canvas(paddedBitmap);
                    canvas1.drawBitmap(BitmapImg.Spotload(), paddingPx, 10, null);
                    spotLoadList.get(i).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.BLACK), 0, 0)));
                }
            }

            if (loadFlowOverVoltageSectionId != null && overVoltageColors != null && !loadFlowOverVoltageSectionId.isEmpty()) {
                for (int i = 0; i < loadFlowOverVoltageSectionId.size(); i++) {

                    if (CaSectionId.containsKey(loadFlowOverVoltageSectionId.get(i))) {
                        Objects.requireNonNull(CaSectionId.get(loadFlowOverVoltageSectionId.get(i))).setColor(Color.parseColor(overVoltageColors));
                    }

                    if (OhSectionId.containsKey(loadFlowOverVoltageSectionId.get(i))) {
                        Objects.requireNonNull(OhSectionId.get(loadFlowOverVoltageSectionId.get(i))).setColor(Color.parseColor(overVoltageColors));
                    }

                    if (UnBalSectionId.containsKey(loadFlowOverVoltageSectionId.get(i))) {
                        Objects.requireNonNull(UnBalSectionId.get(loadFlowOverVoltageSectionId.get(i))).setColor(Color.parseColor(overVoltageColors));
                    }

                    if (breakerSectionId.containsKey(loadFlowOverVoltageSectionId.get(i))) {
                        @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.breaker);
                        Bitmap bitmap = drawableToBitmap(drawable);
                        Objects.requireNonNull(breakerSectionId.get(loadFlowOverVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 0, 95)));
                    }

                    if (transformerSectionId.containsKey(loadFlowOverVoltageSectionId.get(i))) {
                        @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.transformer);
                        Bitmap bitmap = drawableToBitmap(drawable);
                        Objects.requireNonNull(transformerSectionId.get(loadFlowOverVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 0, 93)));
                    }

                    if (fuseSectionId.containsKey(loadFlowOverVoltageSectionId.get(i))) {
                        Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "L", 90f, Color.BLACK);
                        Objects.requireNonNull(fuseSectionId.get(loadFlowOverVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 0, 0)));
                    }

                    if (switchSectionId.containsKey(loadFlowOverVoltageSectionId.get(i))) {
                        Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "D", 70f, Color.BLACK);
                        int paddingLeft = 0; // Left padding in pixels
                        int paddingTop = 0; // Top padding in pixels
                        int paddingRight = 0; // Right padding in pixels
                        int paddingBottom = 12; // Bottom padding in pixels
                        int newWidth = bitmap.getWidth() + paddingLeft + paddingRight;
                        int newHeight = bitmap.getHeight() + paddingTop + paddingBottom;
                        Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, bitmap.getConfig());
                        Canvas canva = new Canvas(paddedBitmap);
                        canva.drawBitmap(bitmap, paddingLeft, paddingTop, null);
                        Objects.requireNonNull(switchSectionId.get(loadFlowOverVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(overVoltageColors)), 0, 0)));
                    }

                    if (capacitorSectionId.containsKey(loadFlowOverVoltageSectionId.get(i))) {
                        Objects.requireNonNull(capacitorSectionId.get(loadFlowOverVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(overVoltageColors)), 90, 0)));
                    }

                    if (spotLoadSectionId.containsKey(loadFlowOverVoltageSectionId.get(i))) {
                        if (spLineSectionList.contains(loadFlowOverVoltageSectionId.get(i))) {
                            int paddingPx = 0;
                            Bitmap paddedBitmap = Bitmap.createBitmap(BitmapImg.Spotload().getWidth() + paddingPx * 2, // Add padding to both sides
                                    BitmapImg.Spotload().getHeight() + paddingPx * 2, Bitmap.Config.ARGB_8888);
                            Canvas canvas1 = new Canvas(paddedBitmap);
                            canvas1.drawBitmap(BitmapImg.Spotload(), paddingPx, 10, null);
                            Objects.requireNonNull(spotLoadSectionId.get(loadFlowOverVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(overVoltageColors)), 0, 0)));
                        } else {
                            Objects.requireNonNull(spotLoadSectionId.get(loadFlowOverVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Spotload(), Color.parseColor(overVoltageColors)), 0, 35)));
                        }
                    }

                }
            }

            if (loadFlowUnderVoltageSectionId != null && underVoltageColors != null && !loadFlowUnderVoltageSectionId.isEmpty()) {
                for (int i = 0; i < loadFlowUnderVoltageSectionId.size(); i++) {

                    if (CaSectionId.containsKey(loadFlowUnderVoltageSectionId.get(i))) {
                        Objects.requireNonNull(CaSectionId.get(loadFlowUnderVoltageSectionId.get(i))).setColor(Color.parseColor(underVoltageColors));
                    }

                    if (OhSectionId.containsKey(loadFlowUnderVoltageSectionId.get(i))) {
                        Objects.requireNonNull(OhSectionId.get(loadFlowUnderVoltageSectionId.get(i))).setColor(Color.parseColor(underVoltageColors));
                    }

                    if (UnBalSectionId.containsKey(loadFlowUnderVoltageSectionId.get(i))) {
                        Objects.requireNonNull(UnBalSectionId.get(loadFlowUnderVoltageSectionId.get(i))).setColor(Color.parseColor(underVoltageColors));
                    }

                    if (breakerSectionId.containsKey(loadFlowUnderVoltageSectionId.get(i))) {
                        @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.breaker);
                        Bitmap bitmap = drawableToBitmap(drawable);
                        Objects.requireNonNull(breakerSectionId.get(loadFlowUnderVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 0, 95)));
                    }

                    if (transformerSectionId.containsKey(loadFlowUnderVoltageSectionId.get(i))) {
                        @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.transformer);
                        Bitmap bitmap = drawableToBitmap(drawable);
                        Objects.requireNonNull(transformerSectionId.get(loadFlowUnderVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 0, 93)));
                    }

                    if (fuseSectionId.containsKey(loadFlowUnderVoltageSectionId.get(i))) {
                        Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "L", 90f, Color.BLACK);
                        Objects.requireNonNull(fuseSectionId.get(loadFlowUnderVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 0, 0)));
                    }

                    if (switchSectionId.containsKey(loadFlowUnderVoltageSectionId.get(i))) {
                        Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "D", 70f, Color.BLACK);
                        int paddingLeft = 0; // Left padding in pixels
                        int paddingTop = 0; // Top padding in pixels
                        int paddingRight = 0; // Right padding in pixels
                        int paddingBottom = 12; // Bottom padding in pixels
                        int newWidth = bitmap.getWidth() + paddingLeft + paddingRight;
                        int newHeight = bitmap.getHeight() + paddingTop + paddingBottom;
                        Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, bitmap.getConfig());
                        Canvas canva = new Canvas(paddedBitmap);
                        canva.drawBitmap(bitmap, paddingLeft, paddingTop, null);
                        Objects.requireNonNull(switchSectionId.get(loadFlowUnderVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(underVoltageColors)), 0, 0)));
                    }

                    if (capacitorSectionId.containsKey(loadFlowUnderVoltageSectionId.get(i))) {
                        Objects.requireNonNull(capacitorSectionId.get(loadFlowUnderVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(underVoltageColors)), 90, 0)));
                    }

                    if (spotLoadSectionId.containsKey(loadFlowUnderVoltageSectionId.get(i))) {
                        if (spLineSectionList.contains(loadFlowUnderVoltageSectionId.get(i))) {
                            int paddingPx = 0;
                            Bitmap paddedBitmap = Bitmap.createBitmap(BitmapImg.Spotload().getWidth() + paddingPx * 2, // Add padding to both sides
                                    BitmapImg.Spotload().getHeight() + paddingPx * 2, Bitmap.Config.ARGB_8888);
                            Canvas canvas1 = new Canvas(paddedBitmap);
                            canvas1.drawBitmap(BitmapImg.Spotload(), paddingPx, 10, null);
                            Objects.requireNonNull(spotLoadSectionId.get(loadFlowUnderVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(underVoltageColors)), 0, 0)));
                        } else {
                            Objects.requireNonNull(spotLoadSectionId.get(loadFlowUnderVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Spotload(), Color.parseColor(underVoltageColors)), 0, 35)));
                        }
                    }

                }
            }

            if (loadFlowOverLoadSectionId != null && overloadColors != null && !loadFlowOverLoadSectionId.isEmpty()) {
                for (int i = 0; i < loadFlowOverLoadSectionId.size(); i++) {

                    if (CaSectionId.containsKey(loadFlowOverLoadSectionId.get(i))) {
                        Objects.requireNonNull(CaSectionId.get(loadFlowOverLoadSectionId.get(i))).setColor(Color.parseColor(overloadColors));
                    }

                    if (OhSectionId.containsKey(loadFlowOverLoadSectionId.get(i))) {
                        Objects.requireNonNull(OhSectionId.get(loadFlowOverLoadSectionId.get(i))).setColor(Color.parseColor(overloadColors));
                    }

                    if (UnBalSectionId.containsKey(loadFlowOverLoadSectionId.get(i))) {
                        Objects.requireNonNull(UnBalSectionId.get(loadFlowOverLoadSectionId.get(i))).setColor(Color.parseColor(overloadColors));
                    }

                    if (breakerSectionId.containsKey(loadFlowOverLoadSectionId.get(i))) {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        Drawable drawable = getResources().getDrawable(R.drawable.breaker);
                        Bitmap bitmap = drawableToBitmap(drawable);
                        Objects.requireNonNull(breakerSectionId.get(loadFlowOverLoadSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 0, 95)));
                    }

                    if (transformerSectionId.containsKey(loadFlowOverLoadSectionId.get(i))) {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        Drawable drawable = getResources().getDrawable(R.drawable.transformer);
                        Bitmap bitmap = drawableToBitmap(drawable);
                        Objects.requireNonNull(transformerSectionId.get(loadFlowOverLoadSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 0, 93)));
                    }

                    if (fuseSectionId.containsKey(loadFlowOverLoadSectionId.get(i))) {
                        Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "L", 90f, Color.BLACK);
                        Objects.requireNonNull(fuseSectionId.get(loadFlowOverLoadSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 0, 0)));
                    }

                    if (switchSectionId.containsKey(loadFlowOverLoadSectionId.get(i))) {
                        Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "D", 70f, Color.BLACK);
                        int paddingLeft = 0; // Left padding in pixels
                        int paddingTop = 0; // Top padding in pixels
                        int paddingRight = 0; // Right padding in pixels
                        int paddingBottom = 12; // Bottom padding in pixels
                        int newWidth = bitmap.getWidth() + paddingLeft + paddingRight;
                        int newHeight = bitmap.getHeight() + paddingTop + paddingBottom;
                        Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, bitmap.getConfig());
                        Canvas canva = new Canvas(paddedBitmap);
                        canva.drawBitmap(bitmap, paddingLeft, paddingTop, null);
                        Objects.requireNonNull(switchSectionId.get(loadFlowOverLoadSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(overloadColors)), 0, 0)));
                    }

                    if (capacitorSectionId.containsKey(loadFlowOverLoadSectionId.get(i))) {
                        Objects.requireNonNull(capacitorSectionId.get(loadFlowOverLoadSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(overloadColors)), 90, 0)));
                    }

                    if (spotLoadSectionId.containsKey(loadFlowOverLoadSectionId.get(i))) {
                        if (spLineSectionList.contains(loadFlowOverLoadSectionId.get(i))) {
                            int paddingPx = 0;
                            Bitmap paddedBitmap = Bitmap.createBitmap(BitmapImg.Spotload().getWidth() + paddingPx * 2, // Add padding to both sides
                                    BitmapImg.Spotload().getHeight() + paddingPx * 2, Bitmap.Config.ARGB_8888);
                            Canvas canvas1 = new Canvas(paddedBitmap);
                            canvas1.drawBitmap(BitmapImg.Spotload(), paddingPx, 10, null);
                            Objects.requireNonNull(spotLoadSectionId.get(loadFlowOverLoadSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(overloadColors)), 0, 0)));
                        } else {
                            Objects.requireNonNull(spotLoadSectionId.get(loadFlowOverLoadSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Spotload(), Color.parseColor(overloadColors)), 0, 35)));
                        }
                    }

                }
            }

        } else if (Config.isShortCircuit) {
            isTracing = false;
            if (CaPolylineList != null && !CaPolylineList.isEmpty()) {
                for (int j = 0; j < CaPolylineList.size(); j++) {
                    CaPolylineList.get(j).getPaint().setColor(Color.BLACK);
                }
            }

            if (ohPolylineList != null && !ohPolylineList.isEmpty()) {
                for (int j = 0; j < ohPolylineList.size(); j++) {
                    ohPolylineList.get(j).getPaint().setColor(Color.BLACK);
                }
            }

            if (unBalPolylineList != null && !unBalPolylineList.isEmpty()) {
                for (int j = 0; j < unBalPolylineList.size(); j++) {
                    unBalPolylineList.get(j).getPaint().setColor(Color.BLACK);
                }
            }

            if (secNodeList != null && !secNodeList.isEmpty()) {
                for (int j = 0; j < secNodeList.size(); j++) {
                    secNodeList.get(j).getPaint().setColor(Color.BLACK);
                }
            }

            if (breakerList != null && !breakerList.isEmpty()) {
                for (int i = 0; i < breakerList.size(); i++) {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    Drawable drawable = getResources().getDrawable(R.drawable.breaker);
                    Bitmap bitmap = drawableToBitmap(drawable);
                    breakerList.get(i).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 95)));
                }
            }

            if (transformerList != null && !transformerList.isEmpty()) {
                for (int i = 0; i < transformerList.size(); i++) {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    Drawable drawable = getResources().getDrawable(R.drawable.transformer);
                    Bitmap bitmap = drawableToBitmap(drawable);
                    transformerList.get(i).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 93)));
                }
            }

            if (fuseList != null && !fuseList.isEmpty()) {
                for (int i = 0; i < fuseList.size(); i++) {
                    Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "L", 90f, Color.BLACK);
                    fuseList.get(i).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 0)));
                }
            }

            if (switchedList != null && !switchedList.isEmpty()) {
                for (int i = 0; i < switchedList.size(); i++) {
                    Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "D", 70f, Color.BLACK);
                    int paddingLeft = 0; // Left padding in pixels
                    int paddingTop = 0; // Top padding in pixels
                    int paddingRight = 0; // Right padding in pixels
                    int paddingBottom = 12; // Bottom padding in pixels
                    int newWidth = bitmap.getWidth() + paddingLeft + paddingRight;
                    int newHeight = bitmap.getHeight() + paddingTop + paddingBottom;
                    Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, bitmap.getConfig());
                    Canvas canva = new Canvas(paddedBitmap);
                    canva.drawBitmap(bitmap, paddingLeft, paddingTop, null);
                    switchedList.get(i).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.BLACK), 0, 0)));
                }
            }

            if (capacitorList != null && !capacitorList.isEmpty()) {
                for (int i = 0; i < capacitorList.size(); i++) {
                    capacitorList.get(i).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.BLACK), 90, 0)));
                }
            }

            if (spotLoadList != null && !spotLoadList.isEmpty()) {
                for (int i = 0; i < spotLoadList.size(); i++) {
                    int paddingPx = 0;
                    Bitmap paddedBitmap = Bitmap.createBitmap(BitmapImg.Spotload().getWidth() + paddingPx * 2, // Add padding to both sides
                            BitmapImg.Spotload().getHeight() + paddingPx * 2, Bitmap.Config.ARGB_8888);
                    Canvas canvas1 = new Canvas(paddedBitmap);
                    canvas1.drawBitmap(BitmapImg.Spotload(), paddingPx, 10, null);
                    spotLoadList.get(i).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.BLACK), 0, 0)));
                }
            }

            if (shortCircuitRatingSectionId != null && ratingColors != null && !shortCircuitRatingSectionId.isEmpty()) {
                for (int i = 0; i < shortCircuitRatingSectionId.size(); i++) {

                    if (CaSectionId.containsKey(shortCircuitRatingSectionId.get(i))) {
                        Objects.requireNonNull(CaSectionId.get(shortCircuitRatingSectionId.get(i))).setColor(Color.parseColor(ratingColors));
                    }

                    if (OhSectionId.containsKey(shortCircuitRatingSectionId.get(i))) {
                        Objects.requireNonNull(OhSectionId.get(shortCircuitRatingSectionId.get(i))).setColor(Color.parseColor(ratingColors));
                    }

                    if (UnBalSectionId.containsKey(shortCircuitRatingSectionId.get(i))) {
                        Objects.requireNonNull(UnBalSectionId.get(shortCircuitRatingSectionId.get(i))).setColor(Color.parseColor(ratingColors));
                    }

                    if (breakerSectionId.containsKey(shortCircuitRatingSectionId.get(i))) {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        Drawable drawable = getResources().getDrawable(R.drawable.breaker);
                        Bitmap bitmap = drawableToBitmap(drawable);
                        Objects.requireNonNull(breakerSectionId.get(shortCircuitRatingSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(ratingColors)), 0, 95)));
                    }

                    if (transformerSectionId.containsKey(shortCircuitRatingSectionId.get(i))) {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        Drawable drawable = getResources().getDrawable(R.drawable.transformer);
                        Bitmap bitmap = drawableToBitmap(drawable);
                        Objects.requireNonNull(transformerSectionId.get(shortCircuitRatingSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(ratingColors)), 0, 93)));
                    }

                    if (fuseSectionId.containsKey(shortCircuitRatingSectionId.get(i))) {
                        Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "L", 90f, Color.BLACK);
                        Objects.requireNonNull(fuseSectionId.get(shortCircuitRatingSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(ratingColors)), 0, 0)));
                    }

                    if (switchSectionId.containsKey(shortCircuitRatingSectionId.get(i))) {
                        Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "D", 70f, Color.BLACK);
                        int paddingLeft = 0; // Left padding in pixels
                        int paddingTop = 0; // Top padding in pixels
                        int paddingRight = 0; // Right padding in pixels
                        int paddingBottom = 12; // Bottom padding in pixels
                        int newWidth = bitmap.getWidth() + paddingLeft + paddingRight;
                        int newHeight = bitmap.getHeight() + paddingTop + paddingBottom;
                        Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, bitmap.getConfig());
                        Canvas canva = new Canvas(paddedBitmap);
                        canva.drawBitmap(bitmap, paddingLeft, paddingTop, null);
                        Objects.requireNonNull(switchSectionId.get(shortCircuitRatingSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(ratingColors)), 0, 0)));
                    }

                    if (capacitorSectionId.containsKey(shortCircuitRatingSectionId.get(i))) {
                        Objects.requireNonNull(capacitorSectionId.get(shortCircuitRatingSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(ratingColors)), 90, 0)));
                    }

                    if (spotLoadSectionId.containsKey(shortCircuitRatingSectionId.get(i))) {
                        if (spLineSectionList.contains(shortCircuitRatingSectionId.get(i))) {
                            int paddingPx = 0;
                            Bitmap paddedBitmap = Bitmap.createBitmap(BitmapImg.Spotload().getWidth() + paddingPx * 2, // Add padding to both sides
                                    BitmapImg.Spotload().getHeight() + paddingPx * 2, Bitmap.Config.ARGB_8888);
                            Canvas canvas1 = new Canvas(paddedBitmap);
                            canvas1.drawBitmap(BitmapImg.Spotload(), paddingPx, 10, null);
                            Objects.requireNonNull(spotLoadSectionId.get(shortCircuitRatingSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(ratingColors)), 0, 0)));
                        } else {
                            Objects.requireNonNull(spotLoadSectionId.get(shortCircuitRatingSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Spotload(), Color.parseColor(ratingColors)), 0, 35)));
                        }
                    }

                }
            }

            if (shortCircuitOverVoltageSectionId != null && overVoltageColors != null && !shortCircuitOverVoltageSectionId.isEmpty()) {
                for (int i = 0; i < shortCircuitOverVoltageSectionId.size(); i++) {

                    if (CaSectionId.containsKey(shortCircuitOverVoltageSectionId.get(i))) {
                        Objects.requireNonNull(CaSectionId.get(shortCircuitOverVoltageSectionId.get(i))).setColor(Color.parseColor(overVoltageColors));
                    }

                    if (OhSectionId.containsKey(shortCircuitOverVoltageSectionId.get(i))) {
                        Objects.requireNonNull(OhSectionId.get(shortCircuitOverVoltageSectionId.get(i))).setColor(Color.parseColor(overVoltageColors));
                    }

                    if (UnBalSectionId.containsKey(shortCircuitOverVoltageSectionId.get(i))) {
                        Objects.requireNonNull(UnBalSectionId.get(shortCircuitOverVoltageSectionId.get(i))).setColor(Color.parseColor(overVoltageColors));
                    }

                    if (breakerSectionId.containsKey(shortCircuitOverVoltageSectionId.get(i))) {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        Drawable drawable = getResources().getDrawable(R.drawable.breaker);
                        Bitmap bitmap = drawableToBitmap(drawable);
                        Objects.requireNonNull(breakerSectionId.get(shortCircuitOverVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 0, 95)));
                    }

                    if (transformerSectionId.containsKey(shortCircuitOverVoltageSectionId.get(i))) {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        Drawable drawable = getResources().getDrawable(R.drawable.transformer);
                        Bitmap bitmap = drawableToBitmap(drawable);
                        Objects.requireNonNull(transformerSectionId.get(shortCircuitOverVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 0, 93)));
                    }

                    if (fuseSectionId.containsKey(shortCircuitOverVoltageSectionId.get(i))) {
                        Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "L", 90f, Color.BLACK);
                        Objects.requireNonNull(fuseSectionId.get(shortCircuitOverVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 0, 0)));
                    }

                    if (switchSectionId.containsKey(shortCircuitOverVoltageSectionId.get(i))) {
                        Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "D", 70f, Color.BLACK);
                        int paddingLeft = 0; // Left padding in pixels
                        int paddingTop = 0; // Top padding in pixels
                        int paddingRight = 0; // Right padding in pixels
                        int paddingBottom = 12; // Bottom padding in pixels
                        int newWidth = bitmap.getWidth() + paddingLeft + paddingRight;
                        int newHeight = bitmap.getHeight() + paddingTop + paddingBottom;
                        Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, bitmap.getConfig());
                        Canvas canva = new Canvas(paddedBitmap);
                        canva.drawBitmap(bitmap, paddingLeft, paddingTop, null);
                        Objects.requireNonNull(switchSectionId.get(shortCircuitOverVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(overVoltageColors)), 0, 0)));
                    }

                    if (capacitorSectionId.containsKey(shortCircuitOverVoltageSectionId.get(i))) {
                        Objects.requireNonNull(capacitorSectionId.get(shortCircuitOverVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(overVoltageColors)), 90, 0)));
                    }

                    if (spotLoadSectionId.containsKey(shortCircuitOverVoltageSectionId.get(i))) {
                        if (spLineSectionList.contains(shortCircuitOverVoltageSectionId.get(i))) {
                            int paddingPx = 0;
                            Bitmap paddedBitmap = Bitmap.createBitmap(BitmapImg.Spotload().getWidth() + paddingPx * 2, // Add padding to both sides
                                    BitmapImg.Spotload().getHeight() + paddingPx * 2, Bitmap.Config.ARGB_8888);
                            Canvas canvas1 = new Canvas(paddedBitmap);
                            canvas1.drawBitmap(BitmapImg.Spotload(), paddingPx, 10, null);
                            Objects.requireNonNull(spotLoadSectionId.get(shortCircuitOverVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(overVoltageColors)), 0, 0)));
                        } else {
                            Objects.requireNonNull(spotLoadSectionId.get(shortCircuitOverVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Spotload(), Color.parseColor(overVoltageColors)), 0, 35)));
                        }
                    }

                }
            }

            if (shortCircuitUnderVoltageSectionId != null && underVoltageColors != null && !shortCircuitUnderVoltageSectionId.isEmpty()) {
                for (int i = 0; i < shortCircuitUnderVoltageSectionId.size(); i++) {

                    if (CaSectionId.containsKey(shortCircuitUnderVoltageSectionId.get(i))) {
                        Objects.requireNonNull(CaSectionId.get(shortCircuitUnderVoltageSectionId.get(i))).setColor(Color.parseColor(underVoltageColors));
                    }

                    if (OhSectionId.containsKey(shortCircuitUnderVoltageSectionId.get(i))) {
                        Objects.requireNonNull(OhSectionId.get(shortCircuitUnderVoltageSectionId.get(i))).setColor(Color.parseColor(underVoltageColors));
                    }

                    if (UnBalSectionId.containsKey(shortCircuitUnderVoltageSectionId.get(i))) {
                        Objects.requireNonNull(UnBalSectionId.get(shortCircuitUnderVoltageSectionId.get(i))).setColor(Color.parseColor(underVoltageColors));
                    }

                    if (breakerSectionId.containsKey(shortCircuitUnderVoltageSectionId.get(i))) {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        Drawable drawable = getResources().getDrawable(R.drawable.breaker);
                        Bitmap bitmap = drawableToBitmap(drawable);
                        Objects.requireNonNull(breakerSectionId.get(shortCircuitUnderVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 0, 95)));
                    }

                    if (transformerSectionId.containsKey(shortCircuitUnderVoltageSectionId.get(i))) {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        Drawable drawable = getResources().getDrawable(R.drawable.transformer);
                        Bitmap bitmap = drawableToBitmap(drawable);
                        Objects.requireNonNull(transformerSectionId.get(shortCircuitUnderVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 0, 93)));
                    }

                    if (fuseSectionId.containsKey(shortCircuitUnderVoltageSectionId.get(i))) {
                        Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "L", 90f, Color.BLACK);
                        Objects.requireNonNull(fuseSectionId.get(shortCircuitUnderVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 0, 0)));
                    }

                    if (switchSectionId.containsKey(shortCircuitUnderVoltageSectionId.get(i))) {
                        Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "D", 70f, Color.BLACK);
                        int paddingLeft = 0; // Left padding in pixels
                        int paddingTop = 0; // Top padding in pixels
                        int paddingRight = 0; // Right padding in pixels
                        int paddingBottom = 12; // Bottom padding in pixels
                        int newWidth = bitmap.getWidth() + paddingLeft + paddingRight;
                        int newHeight = bitmap.getHeight() + paddingTop + paddingBottom;
                        Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, bitmap.getConfig());
                        Canvas canva = new Canvas(paddedBitmap);
                        canva.drawBitmap(bitmap, paddingLeft, paddingTop, null);
                        Objects.requireNonNull(switchSectionId.get(shortCircuitUnderVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(underVoltageColors)), 0, 0)));
                    }

                    if (capacitorSectionId.containsKey(shortCircuitUnderVoltageSectionId.get(i))) {
                        Objects.requireNonNull(capacitorSectionId.get(shortCircuitUnderVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(underVoltageColors)), 90, 0)));
                    }

                    if (spotLoadSectionId.containsKey(shortCircuitUnderVoltageSectionId.get(i))) {
                        if (spLineSectionList.contains(shortCircuitUnderVoltageSectionId.get(i))) {
                            int paddingPx = 0;
                            Bitmap paddedBitmap = Bitmap.createBitmap(BitmapImg.Spotload().getWidth() + paddingPx * 2, // Add padding to both sides
                                    BitmapImg.Spotload().getHeight() + paddingPx * 2, Bitmap.Config.ARGB_8888);
                            Canvas canvas1 = new Canvas(paddedBitmap);
                            canvas1.drawBitmap(BitmapImg.Spotload(), paddingPx, 10, null);
                            Objects.requireNonNull(spotLoadSectionId.get(shortCircuitUnderVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(underVoltageColors)), 0, 0)));
                        } else {
                            Objects.requireNonNull(spotLoadSectionId.get(shortCircuitUnderVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Spotload(), Color.parseColor(underVoltageColors)), 0, 35)));
                        }
                    }

                }
            }

            if (shortCircuitOverLoadSectionId != null && overloadColors != null && !shortCircuitOverLoadSectionId.isEmpty()) {
                for (int i = 0; i < shortCircuitOverLoadSectionId.size(); i++) {

                    if (CaSectionId.containsKey(shortCircuitOverLoadSectionId.get(i))) {
                        Objects.requireNonNull(CaSectionId.get(shortCircuitOverLoadSectionId.get(i))).setColor(Color.parseColor(overloadColors));
                    }

                    if (OhSectionId.containsKey(shortCircuitOverLoadSectionId.get(i))) {
                        Objects.requireNonNull(OhSectionId.get(shortCircuitOverLoadSectionId.get(i))).setColor(Color.parseColor(overloadColors));
                    }

                    if (UnBalSectionId.containsKey(shortCircuitOverLoadSectionId.get(i))) {
                        Objects.requireNonNull(UnBalSectionId.get(shortCircuitOverLoadSectionId.get(i))).setColor(Color.parseColor(overloadColors));
                    }

                    if (breakerSectionId.containsKey(shortCircuitOverLoadSectionId.get(i))) {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        Drawable drawable = getResources().getDrawable(R.drawable.breaker);
                        Bitmap bitmap = drawableToBitmap(drawable);
                        Objects.requireNonNull(breakerSectionId.get(shortCircuitOverLoadSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 0, 95)));
                    }

                    if (transformerSectionId.containsKey(shortCircuitOverLoadSectionId.get(i))) {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        Drawable drawable = getResources().getDrawable(R.drawable.transformer);
                        Bitmap bitmap = drawableToBitmap(drawable);
                        Objects.requireNonNull(transformerSectionId.get(shortCircuitOverLoadSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 0, 93)));
                    }

                    if (fuseSectionId.containsKey(shortCircuitOverLoadSectionId.get(i))) {
                        Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "L", 90f, Color.BLACK);
                        Objects.requireNonNull(fuseSectionId.get(shortCircuitOverLoadSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 0, 0)));
                    }

                    if (switchSectionId.containsKey(shortCircuitOverLoadSectionId.get(i))) {
                        Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "D", 70f, Color.BLACK);
                        int paddingLeft = 0; // Left padding in pixels
                        int paddingTop = 0; // Top padding in pixels
                        int paddingRight = 0; // Right padding in pixels
                        int paddingBottom = 12; // Bottom padding in pixels
                        int newWidth = bitmap.getWidth() + paddingLeft + paddingRight;
                        int newHeight = bitmap.getHeight() + paddingTop + paddingBottom;
                        Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, bitmap.getConfig());
                        Canvas canva = new Canvas(paddedBitmap);
                        canva.drawBitmap(bitmap, paddingLeft, paddingTop, null);
                        Objects.requireNonNull(switchSectionId.get(shortCircuitOverLoadSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(overloadColors)), 0, 0)));
                    }

                    if (capacitorSectionId.containsKey(shortCircuitOverLoadSectionId.get(i))) {
                        Objects.requireNonNull(capacitorSectionId.get(shortCircuitOverLoadSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(overloadColors)), 90, 0)));
                    }

                    if (spotLoadSectionId.containsKey(shortCircuitOverLoadSectionId.get(i))) {
                        if (spLineSectionList.contains(shortCircuitOverLoadSectionId.get(i))) {
                            int paddingPx = 0;
                            Bitmap paddedBitmap = Bitmap.createBitmap(BitmapImg.Spotload().getWidth() + paddingPx * 2, // Add padding to both sides
                                    BitmapImg.Spotload().getHeight() + paddingPx * 2, Bitmap.Config.ARGB_8888);
                            Canvas canvas1 = new Canvas(paddedBitmap);
                            canvas1.drawBitmap(BitmapImg.Spotload(), paddingPx, 10, null);
                            Objects.requireNonNull(spotLoadSectionId.get(shortCircuitOverLoadSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(overloadColors)), 0, 0)));
                        } else {
                            Objects.requireNonNull(spotLoadSectionId.get(shortCircuitOverLoadSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Spotload(), Color.parseColor(overloadColors)), 0, 35)));
                        }
                    }
                }
            }
        } else {
            isTracing = false;
            if (CaPolylineList != null && !CaPolylineList.isEmpty()) {
                for (int j = 0; j < CaPolylineList.size(); j++) {
                    CaPolylineList.get(j).getPaint().setColor(Color.RED);
                }
            }

            if (ohPolylineList != null && !ohPolylineList.isEmpty()) {
                for (int j = 0; j < ohPolylineList.size(); j++) {
                    ohPolylineList.get(j).getPaint().setColor(Color.BLUE);
                }
            }

            if (unBalPolylineList != null && !unBalPolylineList.isEmpty()) {
                for (int j = 0; j < unBalPolylineList.size(); j++) {
                    unBalPolylineList.get(j).getPaint().setColor(Color.BLACK);
                }
            }

            if (secNodeList != null && !secNodeList.isEmpty()) {
                for (int j = 0; j < secNodeList.size(); j++) {
                    secNodeList.get(j).getPaint().setColor(Color.BLACK);
                }
            }
        }
    }

    private void addNewSections(List<GeoPoint> geoPoint) {
        GeoPoint lastGeoPoint = new GeoPoint(geoPoint.get(geoPoint.size() - 1));
        Polyline line = new Polyline();
        line.setPoints(geoPoint);
        line.getOutlinePaint().setColor(Color.parseColor("#14C61B"));
        line.getOutlinePaint().setStrokeWidth(3);
        line.setWidth(3f);
        line.setColor(Color.parseColor("#14C61B"));
        binding.map.getOverlayManager().add(line);
        newPolyLineList.add(line);
        binding.map.invalidate();
        coordinateList.add(lastGeoPoint);
    }

    private void highlightDevice(Marker marker, String type, String DeviceNumber, String SectionID, int location) {
        try {
            if (previousSelectedDevice != null && deviceType != null && highlightSectionID != null && highlightDeviceNumber != null) {
                previousSelectedDevice.closeInfoWindow();
                switch (deviceType) {
                    case "8": {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        Drawable drawable = getResources().getDrawable(R.drawable.breaker);
                        Bitmap bitmap = drawableToBitmap(drawable);
                        if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                            if (secNodeSectionId.get(highlightSectionID) != null) {
                                secNodeSectionId.get(highlightSectionID).getPaint().setColor(Color.BLACK);
                                highlightSectionID = null;
                            }
                        }
                        if (deviceLocation == 2) {
                            previousSelectedDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 135, 0)));
                            previousSelectedDevice = null;
                            deviceType = null;
                            deviceLocation = 0;
                        } else if (deviceLocation == 1) {
                            previousSelectedDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 135)));
                            previousSelectedDevice = null;
                            deviceType = null;
                            deviceLocation = 0;
                        } else {
                            previousSelectedDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 0)));
                            previousSelectedDevice = null;
                            deviceType = null;
                            deviceLocation = 0;
                        }
                        break;
                    }
                    case "5": {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        Drawable drawable = getResources().getDrawable(R.drawable.transformer);
                        Bitmap bitmap = drawableToBitmap(drawable);
                        if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                            if (secNodeSectionId.get(highlightSectionID) != null) {
                                secNodeSectionId.get(highlightSectionID).getPaint().setColor(Color.BLACK);
                                highlightSectionID = null;
                            }
                        }
                        if (deviceLocation == 2) {
                            previousSelectedDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 135, 0)));
                            previousSelectedDevice = null;
                            deviceType = null;
                            deviceLocation = 0;
                        } else if (deviceLocation == 1) {
                            previousSelectedDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 135)));
                            previousSelectedDevice = null;
                            deviceType = null;
                            deviceLocation = 0;
                        } else {
                            previousSelectedDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 0)));
                            previousSelectedDevice = null;
                            deviceType = null;
                            deviceLocation = 0;
                        }
                        break;
                    }
                    case "14": {
                        if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                            if (secNodeSectionId.get(highlightSectionID) != null) {
                                secNodeSectionId.get(highlightSectionID).getPaint().setColor(Color.BLACK);
                                highlightSectionID = null;
                            }
                        }
                        Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "L", 90f, Color.BLACK);
                        if (deviceLocation == 2) {
                            previousSelectedDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 95, 0)));
                            previousSelectedDevice = null;
                            deviceType = null;
                            deviceLocation = 0;
                        } else if (deviceLocation == 1) {
                            previousSelectedDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 95)));
                            previousSelectedDevice = null;
                            deviceType = null;
                            deviceLocation = 0;
                        } else {
                            previousSelectedDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 0)));
                            previousSelectedDevice = null;
                            deviceType = null;
                            deviceLocation = 0;
                        }
                        break;
                    }
                    case "13": {
                        Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "D", 70f, Color.BLACK);
                        int paddingLeft = 0; // Left padding in pixels

                        int paddingTop = 0; // Top padding in pixels

                        int paddingRight = 0; // Right padding in pixels

                        int paddingBottom = 12; // Bottom padding in pixels

                        int newWidth = bitmap.getWidth() + paddingLeft + paddingRight;
                        int newHeight = bitmap.getHeight() + paddingTop + paddingBottom;
                        Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, bitmap.getConfig());
                        Canvas canva = new Canvas(paddedBitmap);
                        canva.drawBitmap(bitmap, paddingLeft, paddingTop, null);
                        if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                            if (secNodeSectionId.get(highlightSectionID) != null) {
                                secNodeSectionId.get(highlightSectionID).getPaint().setColor(Color.BLACK);
                                highlightSectionID = null;
                            }
                        }
                        if (deviceLocation == 2) {
                            previousSelectedDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.BLACK), 95, 0)));
                            previousSelectedDevice = null;
                            deviceType = null;
                            deviceLocation = 0;
                        } else if (deviceLocation == 1) {
                            previousSelectedDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.BLACK), 0, 95)));
                            previousSelectedDevice = null;
                            deviceType = null;
                            deviceLocation = 0;
                        } else {
                            previousSelectedDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.BLACK), 0, 0)));
                            previousSelectedDevice = null;
                            deviceType = null;
                            deviceLocation = 0;
                        }
                        break;

                    }
                    case "17":
                        if (!secNodeSectionId.isEmpty()) {
                            if (secNodeSectionId.get(highlightSectionID) != null) {
                                secNodeSectionId.get(highlightSectionID).getPaint().setColor(Color.BLACK);
                                highlightSectionID = null;
                            }
                        }

                        previousSelectedDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.BLACK), 0, 0)));
                        previousSelectedDevice = null;
                        deviceType = null;
                        deviceLocation = 0;
                        break;

                    case "20": {
                        int paddingPx = 0;
                        Bitmap paddedBitmap = Bitmap.createBitmap(BitmapImg.Spotload().getWidth() + paddingPx * 2,
                                BitmapImg.Spotload().getHeight() + paddingPx * 2, Bitmap.Config.ARGB_8888);
                        Canvas canvas1 = new Canvas(paddedBitmap);
                        canvas1.drawBitmap(BitmapImg.Spotload(), paddingPx, 10, null);

                        if (spLineSectionList.contains(highlightDeviceNumber)) {
                            previousSelectedDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.BLACK), 0, 0)));
                        } else {
                            if (deviceLocation == 2) {
                                previousSelectedDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.BLACK), 70, 0)));
                                previousSelectedDevice = null;
                                deviceType = null;
                                highlightDeviceNumber = null;
                                deviceLocation = 0;
                            } else if (deviceLocation == 1) {
                                previousSelectedDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.BLACK), 0, 70)));
                                previousSelectedDevice = null;
                                deviceType = null;
                                highlightDeviceNumber = null;
                                deviceLocation = 0;
                            } else {
                                previousSelectedDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.BLACK), 0, 0)));
                                previousSelectedDevice = null;
                                deviceType = null;
                                highlightDeviceNumber = null;
                                deviceLocation = 0;
                            }
                        }

                        if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                            if (secNodeSectionId.get(highlightSectionID) != null) {
                                secNodeSectionId.get(highlightSectionID).getPaint().setColor(Color.BLACK);
                                highlightSectionID = null;
                            }
                        }
                        break;
                    }
                    case "99": {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        Drawable drawable = getResources().getDrawable(R.drawable.dot);
                        Bitmap bitmap = drawableToBitmap(drawable);
                        previousSelectedDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), changeBgTransparentBitmapColor(bitmap, Color.BLACK)));
                        previousSelectedDevice = null;
                        deviceType = null;
                        deviceLocation = 0;
                        break;
                    }
                }
            }

            if (previousSelectedSection != null && sectionType != null) {
                switch (sectionType) {
                    case "1":
                        previousSelectedSection.setColor(Color.RED);
                        previousSelectedSection = null;
                        sectionType = null;
                        break;
                    case "2":
                        previousSelectedSection.setColor(Color.BLUE);
                        previousSelectedSection = null;
                        sectionType = null;
                        break;
                    case "23":
                        previousSelectedSection.setColor(Color.BLACK);
                        previousSelectedSection = null;
                        sectionType = null;
                        break;
                }
            }

            switch (type) {
                case "8": {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    Drawable drawable = getResources().getDrawable(R.drawable.breaker);
                    Bitmap bitmap = drawableToBitmap(drawable);
                    if (location == 2) {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.GREEN), 135, 0)));
                    } else if (location == 1) {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.GREEN), 0, 135)));
                    } else {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.GREEN), 0, 0)));
                    }

                    if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                        if (secNodeSectionId.get(SectionID) != null) {
                            secNodeSectionId.get(SectionID).getPaint().setColor(Color.GREEN);
                        }
                    }
                    break;
                }
                case "5": {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    Drawable drawable = getResources().getDrawable(R.drawable.transformer);
                    Bitmap bitmap = drawableToBitmap(drawable);
                    if (location == 2) {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.GREEN), 135, 0)));
                    } else if (location == 1) {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.GREEN), 0, 135)));
                    } else {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.GREEN), 0, 0)));
                    }
                    if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                        if (secNodeSectionId.get(SectionID) != null) {
                            secNodeSectionId.get(SectionID).getPaint().setColor(Color.GREEN);
                        }
                    }
                    break;
                }
                case "14": {
                    Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "L", 90f, Color.BLACK);
                    if (location == 2) {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.GREEN), 95, 0)));
                    } else if (location == 1) {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.GREEN), 0, 95)));
                    } else {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.GREEN), 0, 0)));
                    }
                    if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                        if (secNodeSectionId.get(SectionID) != null) {
                            secNodeSectionId.get(SectionID).getPaint().setColor(Color.GREEN);
                        }
                    }
                    break;
                }
                case "13": {
                    Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "D", 70f, Color.BLACK);
                    int paddingLeft = 0; // Left padding in pixels

                    int paddingTop = 0; // Top padding in pixels

                    int paddingRight = 0; // Right padding in pixels

                    int paddingBottom = 12; // Bottom padding in pixels

                    int newWidth = bitmap.getWidth() + paddingLeft + paddingRight;
                    int newHeight = bitmap.getHeight() + paddingTop + paddingBottom;
                    Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, Objects.requireNonNull(bitmap.getConfig()));
                    Canvas canva = new Canvas(paddedBitmap);
                    canva.drawBitmap(bitmap, paddingLeft, paddingTop, null);
                    if (location == 2) {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.GREEN), 95, 0)));
                    } else if (location == 1) {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.GREEN), 0, 95)));
                    } else {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.GREEN), 0, 0)));
                    }

                    if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                        if (secNodeSectionId.get(SectionID) != null) {
                            secNodeSectionId.get(SectionID).getPaint().setColor(Color.GREEN);
                        }
                    }
                    break;
                }
                case "17":
                    assert secNodeSectionId != null;
                    marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.GREEN), 0, 0)));
                    if (!secNodeSectionId.isEmpty()) {
                        if (secNodeSectionId.get(SectionID) != null) {
                            secNodeSectionId.get(SectionID).getPaint().setColor(Color.GREEN);
                        }
                    }
                    break;
                case "20": {
                    int paddingPx = 0;
                    Bitmap paddedBitmap = Bitmap.createBitmap(BitmapImg.Spotload().getWidth() + paddingPx * 2, // Add padding to both sides
                            BitmapImg.Spotload().getHeight() + paddingPx * 2, Bitmap.Config.ARGB_8888);
                    Canvas canvas1 = new Canvas(paddedBitmap);
                    canvas1.drawBitmap(BitmapImg.Spotload(), paddingPx, 10, null);

                    if (spLineSectionList.contains(DeviceNumber)) {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.GREEN), 0, 0)));
                    } else {
                        if (location == 2) {
                            marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.GREEN), 70, 0)));
                        } else if (location == 1) {
                            marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.GREEN), 0, 70)));
                        } else {
                            marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.GREEN), 0, 0)));
                        }

                    }
                    if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                        if (secNodeSectionId.get(SectionID) != null) {
                            secNodeSectionId.get(SectionID).getPaint().setColor(Color.GREEN);
                        }
                    }
                    break;
                }
                case "99":
                    @SuppressLint("UseCompatLoadingForDrawables")
                    Drawable drawable1 = getResources().getDrawable(R.drawable.node);
                    Bitmap bitmap1 = drawableToBitmap(drawable1);
                    marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), changeBgTransparentBitmapColor(bitmap1, Color.GREEN)));
                    break;
            }

            previousSelectedDevice = marker;
            highlightSectionID = SectionID;
            highlightDeviceNumber = DeviceNumber;
            deviceType = type;
            deviceLocation = location;
        } catch (Exception e) {
            Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
        }
    }

    private void highlightSection(Polyline polyline, String type, int location) {
        try {
            if (previousSelectedSection != null && sectionType != null) {
                switch (sectionType) {
                    case "1":
                        previousSelectedSection.setColor(Color.RED);
                        previousSelectedSection = null;
                        sectionType = null;
                        break;
                    case "2":
                        previousSelectedSection.setColor(Color.BLUE);
                        previousSelectedSection = null;
                        sectionType = null;
                        break;
                    case "23":
                        previousSelectedSection.setColor(Color.BLACK);
                        previousSelectedSection = null;
                        sectionType = null;
                        break;
                }
            }

            if (previousSelectedDevice != null && deviceType != null) {
                previousSelectedDevice.closeInfoWindow();
                switch (deviceType) {
                    case "8": {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        Drawable drawable = getResources().getDrawable(R.drawable.breaker);
                        Bitmap bitmap = drawableToBitmap(drawable);
                        if (highlightSectionID != null && !highlightSectionID.isEmpty()) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(highlightSectionID) != null) {
                                    secNodeSectionId.get(highlightSectionID).getPaint().setColor(Color.BLACK);
                                    highlightSectionID = null;
                                }
                            }
                        }
                        if (deviceLocation == 2) {
                            previousSelectedDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 135, 0)));
                            previousSelectedDevice = null;
                            deviceType = null;
                            deviceLocation = 0;
                        } else if (deviceLocation == 1) {
                            previousSelectedDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 135)));
                            previousSelectedDevice = null;
                            deviceType = null;
                            deviceLocation = 0;
                        } else {
                            previousSelectedDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 0)));
                            previousSelectedDevice = null;
                            deviceType = null;
                            deviceLocation = 0;
                        }
                        break;
                    }
                    case "5": {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        Drawable drawable = getResources().getDrawable(R.drawable.transformer);
                        Bitmap bitmap = drawableToBitmap(drawable);
                        if (highlightSectionID != null && !highlightSectionID.isEmpty()) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(highlightSectionID) != null) {
                                    secNodeSectionId.get(highlightSectionID).getPaint().setColor(Color.BLACK);
                                    highlightSectionID = null;
                                }
                            }
                        }
                        if (deviceLocation == 2) {
                            previousSelectedDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 135, 0)));
                            previousSelectedDevice = null;
                            deviceType = null;
                            deviceLocation = 0;
                        } else if (deviceLocation == 1) {
                            previousSelectedDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 135)));
                            previousSelectedDevice = null;
                            deviceType = null;
                            deviceLocation = 0;
                        } else {
                            previousSelectedDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 0)));
                            previousSelectedDevice = null;
                            deviceType = null;
                            deviceLocation = 0;
                        }
                        break;
                    }
                    case "14": {
                        if (highlightSectionID != null && !highlightSectionID.isEmpty()) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(highlightSectionID) != null) {
                                    secNodeSectionId.get(highlightSectionID).getPaint().setColor(Color.BLACK);
                                    highlightSectionID = null;
                                }
                            }
                        }
                        Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "L", 90f, Color.BLACK);
                        if (deviceLocation == 0) {
                            previousSelectedDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 95, 0)));
                            previousSelectedDevice = null;
                            deviceType = null;
                            deviceLocation = 0;
                        } else if (deviceLocation == 1) {
                            previousSelectedDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 95)));
                            previousSelectedDevice = null;
                            deviceType = null;
                            deviceLocation = 0;
                        } else {
                            previousSelectedDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 0)));
                            previousSelectedDevice = null;
                            deviceType = null;
                            deviceLocation = 0;
                        }
                        break;
                    }
                    case "13": {
                        Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "D", 70f, Color.BLACK);
                        int paddingLeft = 0; // Left padding in pixels

                        int paddingTop = 0; // Top padding in pixels

                        int paddingRight = 0; // Right padding in pixels

                        int paddingBottom = 12; // Bottom padding in pixels

                        int newWidth = bitmap.getWidth() + paddingLeft + paddingRight;
                        int newHeight = bitmap.getHeight() + paddingTop + paddingBottom;
                        Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, bitmap.getConfig());
                        Canvas canva = new Canvas(paddedBitmap);
                        canva.drawBitmap(bitmap, paddingLeft, paddingTop, null);
                        if (highlightSectionID != null && !highlightSectionID.isEmpty()) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(highlightSectionID) != null) {
                                    secNodeSectionId.get(highlightSectionID).getPaint().setColor(Color.BLACK);
                                    highlightSectionID = null;
                                }
                            }
                        }
                        if (deviceLocation == 2) {
                            previousSelectedDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.BLACK), 95, 0)));
                            previousSelectedDevice = null;
                            deviceType = null;
                            deviceLocation = 0;
                        } else if (deviceLocation == 1) {
                            previousSelectedDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.BLACK), 0, 95)));
                            previousSelectedDevice = null;
                            deviceType = null;
                            deviceLocation = 0;
                        } else {
                            previousSelectedDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.BLACK), 0, 0)));
                            previousSelectedDevice = null;
                            deviceType = null;
                            deviceLocation = 0;
                        }
                        break;
                    }
                    case "17":
                        if (highlightSectionID != null && !highlightSectionID.isEmpty()) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(highlightSectionID) != null) {
                                    secNodeSectionId.get(highlightSectionID).getPaint().setColor(Color.BLACK);
                                    highlightSectionID = null;
                                }
                            }
                        }
                        previousSelectedDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.BLACK), 0, 0)));
                        previousSelectedDevice = null;
                        deviceType = null;
                        break;
                    case "20": {
                        int paddingPx = 0;
                        Bitmap paddedBitmap = Bitmap.createBitmap(BitmapImg.Spotload().getWidth() + paddingPx * 2, // Add padding to both sides
                                BitmapImg.Spotload().getHeight() + paddingPx * 2, Bitmap.Config.ARGB_8888);
                        Canvas canvas1 = new Canvas(paddedBitmap);
                        canvas1.drawBitmap(BitmapImg.Spotload(), paddingPx, 10, null);
                        if (highlightSectionID != null && !highlightSectionID.isEmpty()) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(highlightSectionID) != null) {
                                    secNodeSectionId.get(highlightSectionID).getPaint().setColor(Color.BLACK);
                                    highlightSectionID = null;
                                }
                            }
                        }

                        if (spLineSectionList.contains(highlightSectionID)) {
                            previousSelectedDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.GREEN), 0, 0)));
                        } else {
                            if (deviceLocation == 2) {
                                previousSelectedDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.GREEN), 70, 0)));
                                previousSelectedDevice = null;
                                deviceType = null;
                                deviceLocation = 0;
                            } else if (deviceLocation == 1) {
                                previousSelectedDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.GREEN), 0, 70)));
                                previousSelectedDevice = null;
                                deviceType = null;
                                deviceLocation = 0;
                            } else {
                                previousSelectedDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.GREEN), 0, 0)));
                                previousSelectedDevice = null;
                                deviceType = null;
                                deviceLocation = 0;
                            }

                        }
                        break;
                    }
                    case "99": {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        Drawable drawable = getResources().getDrawable(R.drawable.dot);
                        Bitmap bitmap = drawableToBitmap(drawable);
                        previousSelectedDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), changeBgTransparentBitmapColor(bitmap, Color.BLACK)));
                        previousSelectedDevice = null;
                        deviceType = null;
                        break;
                    }
                }
            }

            polyline.setColor(Color.GREEN);
            previousSelectedSection = polyline;
            sectionType = type;
        } catch (Exception e) {
            Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
        }
    }

    private void highlightLoadFlowDevice(Marker marker, String type, String ID, int location) {
        try {
            if (previousLoadFlowDevice != null && loadFlowDeviceType != null && loadFlowDeviceId != null) {
                previousLoadFlowDevice.closeInfoWindow();

                switch (loadFlowDeviceType) {
                    case "8": {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        Drawable drawable = getResources().getDrawable(R.drawable.breaker);
                        Bitmap bitmap = drawableToBitmap(drawable);
                        if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                            if (secNodeSectionId.get(loadFlowDeviceId) != null) {
                                secNodeSectionId.get(loadFlowDeviceId).getPaint().setColor(Color.BLACK);
                            }
                        }

                        if (deviceLocation == 2) {
                            previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 135, 0)));
                        } else if (deviceLocation == 1) {
                            previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 135)));
                        } else {
                            previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 0)));
                        }

                        if (loadFlowOverVoltageDeviceID.containsKey(loadFlowDeviceId) && overVoltageColors != null) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(highlightSectionID) != null) {
                                    secNodeSectionId.get(highlightSectionID).getPaint().setColor(Color.parseColor(overVoltageColors));
                                }
                            }
                            if (deviceLocation == 2) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 135, 0)));
                            } else if (deviceLocation == 1) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 0, 135)));
                            } else {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 0, 0)));
                            }
                        }

                        if (loadFlowUnderVoltageDeviceID.containsKey(loadFlowDeviceId) && underVoltageColors != null) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(highlightSectionID) != null) {
                                    secNodeSectionId.get(highlightSectionID).getPaint().setColor(Color.parseColor(underVoltageColors));
                                }
                            }
                            if (deviceLocation == 2) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 135, 0)));
                            } else if (deviceLocation == 1) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 0, 135)));
                            } else {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 0, 0)));
                            }
                        }

                        if (loadFlowOverLoadDeviceID.containsKey(loadFlowDeviceId) && overloadColors != null) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(highlightSectionID) != null) {
                                    secNodeSectionId.get(highlightSectionID).getPaint().setColor(Color.parseColor(overloadColors));
                                }
                            }
                            if (deviceLocation == 2) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 135, 0)));
                            } else if (deviceLocation == 1) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 0, 135)));
                            } else {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 0, 0)));
                            }
                        }

                        previousLoadFlowDevice = null;
                        loadFlowDeviceType = null;
                        loadFlowDeviceId = null;

                        break;
                    }
                    case "5": {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        Drawable drawable = getResources().getDrawable(R.drawable.transformer);
                        Bitmap bitmap = drawableToBitmap(drawable);
                        if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                            if (secNodeSectionId.get(loadFlowDeviceId) != null) {
                                secNodeSectionId.get(loadFlowDeviceId).getPaint().setColor(Color.BLACK);
                            }
                        }
                        if (deviceLocation == 2) {
                            previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 135, 0)));
                        } else if (deviceLocation == 1) {
                            previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 135)));
                        } else {
                            previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 0)));
                        }


                        if (loadFlowOverVoltageDeviceID.containsKey(loadFlowDeviceId) && overVoltageColors != null) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(loadFlowDeviceId) != null) {
                                    secNodeSectionId.get(loadFlowDeviceId).getPaint().setColor(Color.parseColor(overVoltageColors));
                                    loadFlowDeviceId = null;
                                }
                            }
                            if (deviceLocation == 2) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 135, 0)));
                            } else if (deviceLocation == 1) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 0, 135)));
                            } else {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 0, 0)));
                            }
                        }

                        if (loadFlowUnderVoltageDeviceID.containsKey(loadFlowDeviceId) && underVoltageColors != null) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(loadFlowDeviceId) != null) {
                                    secNodeSectionId.get(loadFlowDeviceId).getPaint().setColor(Color.parseColor(underVoltageColors));
                                    loadFlowDeviceId = null;
                                }
                            }
                            if (deviceLocation == 2) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 135, 0)));
                            } else if (deviceLocation == 1) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 0, 135)));
                            } else {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 0, 0)));
                            }

                        }

                        if (loadFlowOverLoadDeviceID.containsKey(loadFlowDeviceId) && overloadColors != null) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(loadFlowDeviceId) != null) {
                                    secNodeSectionId.get(loadFlowDeviceId).getPaint().setColor(Color.parseColor(overloadColors));
                                    loadFlowDeviceId = null;
                                }
                            }
                            if (deviceLocation == 2) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 135, 0)));
                            } else if (deviceLocation == 1) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 0, 135)));
                            } else {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 0, 0)));
                            }

                        }

                        previousLoadFlowDevice = null;
                        loadFlowDeviceType = null;
                        loadFlowDeviceId = null;

                        break;
                    }
                    case "14": {
                        Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "L", 90f, Color.BLACK);
                        if (deviceLocation == 2) {
                            previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 95, 0)));
                        } else if (deviceLocation == 1) {
                            previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 95)));
                        } else {
                            previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 0)));
                        }

                        if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                            if (secNodeSectionId.get(loadFlowDeviceId) != null) {
                                secNodeSectionId.get(loadFlowDeviceId).getPaint().setColor(Color.BLACK);
                            }
                        }

                        if (loadFlowOverVoltageDeviceID.containsKey(loadFlowDeviceId) && overVoltageColors != null) {
                            if (deviceLocation == 2) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 95, 0)));
                            } else if (deviceLocation == 1) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 0, 95)));
                            } else {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 0, 0)));
                            }
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(loadFlowDeviceId) != null) {
                                    secNodeSectionId.get(loadFlowDeviceId).getPaint().setColor(Color.parseColor(overVoltageColors));
                                }
                            }
                        }

                        if (loadFlowUnderVoltageDeviceID.containsKey(loadFlowDeviceId) && underVoltageColors != null) {
                            if (deviceLocation == 2) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 95, 0)));
                            } else if (deviceLocation == 1) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 0, 95)));
                            } else {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 0, 0)));
                            }
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(loadFlowDeviceId) != null) {
                                    secNodeSectionId.get(loadFlowDeviceId).getPaint().setColor(Color.parseColor(underVoltageColors));
                                }
                            }
                        }

                        if (loadFlowOverLoadDeviceID.containsKey(loadFlowDeviceId) && overloadColors != null) {
                            if (deviceLocation == 2) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 95, 0)));
                            } else if (deviceLocation == 1) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 0, 95)));
                            } else {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 0, 0)));
                            }
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(loadFlowDeviceId) != null) {
                                    secNodeSectionId.get(loadFlowDeviceId).getPaint().setColor(Color.parseColor(overloadColors));
                                }
                            }
                        }

                        previousLoadFlowDevice = null;
                        loadFlowDeviceType = null;
                        loadFlowDeviceId = null;
                        break;
                    }
                    case "13": {
                        Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "D", 70f, Color.BLACK);
                        int paddingLeft = 0; // Left padding in pixels

                        int paddingTop = 0; // Top padding in pixels

                        int paddingRight = 0; // Right padding in pixels

                        int paddingBottom = 12; // Bottom padding in pixels

                        int newWidth = bitmap.getWidth() + paddingLeft + paddingRight;
                        int newHeight = bitmap.getHeight() + paddingTop + paddingBottom;
                        Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, bitmap.getConfig());
                        Canvas canva = new Canvas(paddedBitmap);
                        canva.drawBitmap(bitmap, paddingLeft, paddingTop, null);
                        if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                            if (secNodeSectionId.get(loadFlowDeviceId) != null) {
                                secNodeSectionId.get(loadFlowDeviceId).getPaint().setColor(Color.BLACK);
                            }
                        }
                        if (deviceLocation == 2) {
                            previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.BLACK), 95, 0)));
                        } else if (deviceLocation == 1) {
                            previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.BLACK), 0, 95)));
                        } else {
                            previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.BLACK), 0, 0)));
                        }

                        if (loadFlowOverVoltageDeviceID.containsKey(loadFlowDeviceId) && overVoltageColors != null) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(loadFlowDeviceId) != null) {
                                    secNodeSectionId.get(loadFlowDeviceId).getPaint().setColor(Color.parseColor(overVoltageColors));
                                }
                            }
                            if (deviceLocation == 2) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(overVoltageColors)), 95, 0)));
                            } else if (deviceLocation == 1) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(overVoltageColors)), 0, 95)));
                            } else {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(overVoltageColors)), 0, 0)));
                            }
                        }

                        if (loadFlowUnderVoltageDeviceID.containsKey(loadFlowDeviceId) && underVoltageColors != null) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(loadFlowDeviceId) != null) {
                                    secNodeSectionId.get(loadFlowDeviceId).getPaint().setColor(Color.parseColor(underVoltageColors));
                                }
                            }
                            if (deviceLocation == 2) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(underVoltageColors)), 95, 0)));
                            } else if (deviceLocation == 1) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(underVoltageColors)), 0, 95)));
                            } else {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(underVoltageColors)), 0, 0)));
                            }
                        }

                        if (loadFlowOverLoadDeviceID.containsKey(loadFlowDeviceId) && overloadColors != null) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(loadFlowDeviceId) != null) {
                                    secNodeSectionId.get(loadFlowDeviceId).getPaint().setColor(Color.parseColor(overloadColors));
                                    loadFlowDeviceId = null;
                                }
                            }
                            if (deviceLocation == 2) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(overloadColors)), 95, 0)));
                            } else if (deviceLocation == 1) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(overloadColors)), 0, 95)));
                            } else {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(overloadColors)), 0, 0)));
                            }

                        }

                        previousLoadFlowDevice = null;
                        loadFlowDeviceType = null;
                        loadFlowDeviceId = null;
                        break;
                    }
                    case "61":
                        previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.BLACK), 0, 0)));

                        if (!secNodeSectionId.isEmpty()) {
                            if (secNodeSectionId.get(loadFlowDeviceId) != null) {
                                secNodeSectionId.get(loadFlowDeviceId).getPaint().setColor(Color.BLACK);
                            }
                        }

                        if (loadFlowOverVoltageDeviceID.containsKey(loadFlowDeviceId) && overVoltageColors != null) {
                            previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(overVoltageColors)), 0, 0)));

                            if (!secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(loadFlowDeviceId) != null) {
                                    secNodeSectionId.get(loadFlowDeviceId).getPaint().setColor(Color.parseColor(overVoltageColors));
                                }
                            }
                        }

                        if (loadFlowUnderVoltageDeviceID.containsKey(loadFlowDeviceId) && underVoltageColors != null) {
                            previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(underVoltageColors)), 0, 0)));

                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(loadFlowDeviceId) != null) {
                                    secNodeSectionId.get(loadFlowDeviceId).getPaint().setColor(Color.parseColor(underVoltageColors));
                                }
                            }
                        }

                        if (loadFlowOverLoadDeviceID.containsKey(loadFlowDeviceId) && overloadColors != null) {
                            previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(overloadColors)), 0, 0)));

                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(loadFlowDeviceId) != null) {
                                    secNodeSectionId.get(loadFlowDeviceId).getPaint().setColor(Color.parseColor(overloadColors));
                                }
                            }
                        }

                        previousLoadFlowDevice = null;
                        loadFlowDeviceType = null;
                        loadFlowDeviceId = null;

                        break;
                    case "20": {
                        int paddingPx = 0;
                        Bitmap paddedBitmap = Bitmap.createBitmap(BitmapImg.Spotload().getWidth() + paddingPx * 2, // Add padding to both sides
                                BitmapImg.Spotload().getHeight() + paddingPx * 2, Bitmap.Config.ARGB_8888);
                        Canvas canvas1 = new Canvas(paddedBitmap);
                        canvas1.drawBitmap(BitmapImg.Spotload(), paddingPx, 10, null);
                        if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                            if (secNodeSectionId.get(loadFlowDeviceId) != null) {
                                secNodeSectionId.get(loadFlowDeviceId).getPaint().setColor(Color.BLACK);
                            }
                        }
                        if (spLineSectionList.contains(loadFlowDeviceId)) {
                            previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.BLACK), 0, 0)));
                        } else {
                            if (deviceLocation == 2) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.BLACK), 70, 0)));
                            } else if (deviceLocation == 1) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.BLACK), 0, 70)));
                            } else {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.BLACK), 0, 0)));
                            }
                        }

                        if (loadFlowOverVoltageDeviceID.containsKey(loadFlowDeviceId) && overVoltageColors != null) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(loadFlowDeviceId) != null) {
                                    secNodeSectionId.get(loadFlowDeviceId).getPaint().setColor(Color.parseColor(overVoltageColors));
                                }
                            }

                            if (spLineSectionList.contains(loadFlowDeviceId)) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(overVoltageColors)), 0, 0)));
                            } else {
                                if (deviceLocation == 2) {
                                    previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(overVoltageColors)), 70, 0)));
                                } else if (deviceLocation == 1) {
                                    previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(overVoltageColors)), 0, 70)));
                                } else {
                                    previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(overVoltageColors)), 0, 0)));
                                }
                            }

                        }

                        if (loadFlowUnderVoltageDeviceID.containsKey(loadFlowDeviceId) && underVoltageColors != null) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(loadFlowDeviceId) != null) {
                                    secNodeSectionId.get(loadFlowDeviceId).getPaint().setColor(Color.parseColor(underVoltageColors));
                                }
                            }
                            if (spLineSectionList.contains(loadFlowDeviceId)) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(underVoltageColors)), 0, 0)));
                            } else {
                                if (deviceLocation == 2) {
                                    previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(underVoltageColors)), 70, 0)));
                                } else if (deviceLocation == 1) {
                                    previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(underVoltageColors)), 0, 70)));
                                } else {
                                    previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(underVoltageColors)), 0, 0)));
                                }
                            }
                        }

                        if (loadFlowOverLoadDeviceID.containsKey(loadFlowDeviceId) && overloadColors != null) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(loadFlowDeviceId) != null) {
                                    secNodeSectionId.get(loadFlowDeviceId).getPaint().setColor(Color.parseColor(overloadColors));
                                }
                            }
                            if (spLineSectionList.contains(loadFlowDeviceId)) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(overloadColors)), 0, 0)));
                            } else {
                                if (deviceLocation == 2) {
                                    previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(overloadColors)), 70, 0)));
                                } else if (deviceLocation == 1) {
                                    previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(overloadColors)), 0, 70)));
                                } else {
                                    previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(overloadColors)), 0, 0)));
                                }
                            }

                        }

                        previousLoadFlowDevice = null;
                        loadFlowDeviceType = null;
                        loadFlowDeviceId = null;

                        break;
                    }
                    case "99": {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        Drawable drawable = getResources().getDrawable(R.drawable.dot);
                        Bitmap bitmap = drawableToBitmap(drawable);
                        previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), changeBgTransparentBitmapColor(bitmap, Color.BLACK)));
                        previousLoadFlowDevice = null;
                        loadFlowDeviceType = null;
                        loadFlowDeviceId = null;
                        break;
                    }
                }
            }

            if (loadFlowPreviousSelectedSection != null && loadFlowSectionId != null) {
                loadFlowPreviousSelectedSection.closeInfoWindow();
                loadFlowPreviousSelectedSection.setColor(Color.BLACK);

                if (loadFlowOverVoltageSectionID.containsKey(loadFlowSectionId) && overVoltageColors != null) {
                    loadFlowPreviousSelectedSection.setColor(Color.parseColor(overVoltageColors));
                }

                if (loadFlowUnderVoltageSectionID.containsKey(loadFlowSectionId) && underVoltageColors != null) {
                    loadFlowPreviousSelectedSection.setColor(Color.parseColor(underVoltageColors));
                }

                if (loadFlowOverLoadSectionID.containsKey(loadFlowSectionId) && overloadColors != null) {
                    loadFlowPreviousSelectedSection.setColor(Color.parseColor(overloadColors));
                }

                loadFlowPreviousSelectedSection = null;
                loadFlowSectionId = null;
            }

            switch (type) {
                case "8": {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    Drawable drawable = getResources().getDrawable(R.drawable.breaker);
                    Bitmap bitmap = drawableToBitmap(drawable);
                    if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                        if (secNodeSectionId.get(ID) != null) {
                            secNodeSectionId.get(ID).getPaint().setColor(Color.parseColor("#0ABDE3"));
                        }
                    }
                    if (location == 2) {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor("#0ABDE3")), 135, 0)));
                    } else if (location == 1) {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor("#0ABDE3")), 0, 135)));
                    } else {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor("#0ABDE3")), 0, 0)));
                    }

                    break;
                }
                case "5": {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    Drawable drawable = getResources().getDrawable(R.drawable.transformer);
                    Bitmap bitmap = drawableToBitmap(drawable);
                    if (location == 2) {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor("#0ABDE3")), 135, 0)));
                    } else if (location == 1) {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor("#0ABDE3")), 0, 135)));
                    } else {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor("#0ABDE3")), 0, 0)));
                    }

                    if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                        if (secNodeSectionId.get(ID) != null) {
                            secNodeSectionId.get(ID).getPaint().setColor(Color.parseColor("#0ABDE3"));
                        }
                    }
                    break;
                }
                case "14": {
                    Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "L", 90f, Color.BLACK);
                    if (location == 2) {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor("#0ABDE3")), 95, 0)));
                    } else if (location == 1) {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor("#0ABDE3")), 0, 95)));
                    } else {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor("#0ABDE3")), 0, 0)));
                    }
                    if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                        if (secNodeSectionId.get(ID) != null) {
                            secNodeSectionId.get(ID).getPaint().setColor(Color.parseColor("#0ABDE3"));
                        }
                    }
                    break;
                }
                case "13": {
                    Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "D", 70f, Color.BLACK);
                    int paddingLeft = 0; // Left padding in pixels

                    int paddingTop = 0; // Top padding in pixels

                    int paddingRight = 0; // Right padding in pixels

                    int paddingBottom = 12; // Bottom padding in pixels

                    int newWidth = bitmap.getWidth() + paddingLeft + paddingRight;
                    int newHeight = bitmap.getHeight() + paddingTop + paddingBottom;
                    Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, bitmap.getConfig());
                    Canvas canva = new Canvas(paddedBitmap);
                    canva.drawBitmap(bitmap, paddingLeft, paddingTop, null);
                    if (location == 2) {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor("#0ABDE3")), 95, 0)));
                    } else if (location == 1) {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor("#0ABDE3")), 0, 95)));
                    } else {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor("#0ABDE3")), 0, 0)));
                    }

                    if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                        if (secNodeSectionId.get(ID) != null) {
                            secNodeSectionId.get(ID).getPaint().setColor(Color.parseColor("#0ABDE3"));
                        }
                    }
                    break;
                }
                case "61":
                    marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor("#0ABDE3")), 0, 0)));

                    if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                        if (secNodeSectionId.get(ID) != null) {
                            secNodeSectionId.get(ID).getPaint().setColor(Color.parseColor("#0ABDE3"));
                        }
                    }
                    break;
                case "20": {
                    int paddingPx = 0;
                    Bitmap paddedBitmap = Bitmap.createBitmap(BitmapImg.Spotload().getWidth() + paddingPx * 2, // Add padding to both sides
                            BitmapImg.Spotload().getHeight() + paddingPx * 2, Bitmap.Config.ARGB_8888);
                    Canvas canvas1 = new Canvas(paddedBitmap);
                    canvas1.drawBitmap(BitmapImg.Spotload(), paddingPx, 10, null);
                    if (spLineSectionList.contains(ID)) {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor("#0ABDE3")), 0, 0)));
                    } else {
                        if (location == 2) {
                            marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor("#0ABDE3")), 70, 0)));
                        } else if (location == 1) {
                            marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor("#0ABDE3")), 0, 70)));
                        } else {
                            marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor("#0ABDE3")), 0, 0)));
                        }
                    }

                    if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                        if (secNodeSectionId.get(ID) != null) {
                            secNodeSectionId.get(ID).getPaint().setColor(Color.parseColor("#0ABDE3"));
                        }
                    }
                    break;
                }
                case "99":
                    @SuppressLint("UseCompatLoadingForDrawables")
                    Drawable drawable1 = getResources().getDrawable(R.drawable.node);
                    Bitmap bitmap1 = drawableToBitmap(drawable1);
                    marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), changeBgTransparentBitmapColor(bitmap1, Color.parseColor("#0ABDE3"))));
                    break;
            }

            previousLoadFlowDevice = marker;
            loadFlowDeviceType = type;
            loadFlowDeviceId = ID;
        } catch (Exception e) {
            Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
        }
    }

    private void highlightLoadFlowSection(Polyline polyline, String sectionId, int location) {
        try {
            if (loadFlowPreviousSelectedSection != null && loadFlowSectionId != null) {
                loadFlowPreviousSelectedSection.closeInfoWindow();
                loadFlowPreviousSelectedSection.setColor(Color.BLACK);

                if (loadFlowOverVoltageSectionID.containsKey(loadFlowSectionId) && overVoltageColors != null) {
                    loadFlowPreviousSelectedSection.setColor(Color.parseColor(overVoltageColors));
                }

                if (loadFlowUnderVoltageSectionID.containsKey(loadFlowSectionId) && underVoltageColors != null) {
                    loadFlowPreviousSelectedSection.setColor(Color.parseColor(underVoltageColors));
                }

                if (loadFlowOverLoadSectionID.containsKey(loadFlowSectionId) && overloadColors != null) {
                    loadFlowPreviousSelectedSection.setColor(Color.parseColor(overloadColors));
                }

                loadFlowPreviousSelectedSection = null;
                loadFlowSectionId = null;
            }

            if (previousLoadFlowDevice != null && loadFlowDeviceType != null && loadFlowDeviceId != null) {
                previousLoadFlowDevice.closeInfoWindow();

                switch (loadFlowDeviceType) {
                    case "8": {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        Drawable drawable = getResources().getDrawable(R.drawable.breaker);
                        Bitmap bitmap = drawableToBitmap(drawable);
                        if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                            if (secNodeSectionId.get(loadFlowDeviceId) != null) {
                                secNodeSectionId.get(loadFlowDeviceId).getPaint().setColor(Color.BLACK);
                            }
                        }

                        if (deviceLocation == 2) {
                            previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 135, 0)));
                        } else if (deviceLocation == 1) {
                            previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 135)));
                        } else {
                            previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 0)));
                        }

                        if (loadFlowOverVoltageDeviceID.containsKey(loadFlowDeviceId) && overVoltageColors != null) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(highlightSectionID) != null) {
                                    secNodeSectionId.get(highlightSectionID).getPaint().setColor(Color.parseColor(overVoltageColors));
                                }
                            }
                            if (deviceLocation == 2) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 135, 0)));
                            } else if (deviceLocation == 1) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 0, 135)));
                            } else {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 0, 0)));
                            }
                        }

                        if (loadFlowUnderVoltageDeviceID.containsKey(loadFlowDeviceId) && underVoltageColors != null) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(highlightSectionID) != null) {
                                    secNodeSectionId.get(highlightSectionID).getPaint().setColor(Color.parseColor(underVoltageColors));
                                }
                            }
                            if (deviceLocation == 2) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 135, 0)));
                            } else if (deviceLocation == 1) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 0, 135)));
                            } else {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 0, 0)));
                            }
                        }

                        if (loadFlowOverLoadDeviceID.containsKey(loadFlowDeviceId) && overloadColors != null) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(highlightSectionID) != null) {
                                    secNodeSectionId.get(highlightSectionID).getPaint().setColor(Color.parseColor(overloadColors));
                                }
                            }
                            if (deviceLocation == 2) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 135, 0)));
                            } else if (deviceLocation == 1) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 0, 135)));
                            } else {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 0, 0)));
                            }
                        }

                        previousLoadFlowDevice = null;
                        loadFlowDeviceType = null;
                        loadFlowDeviceId = null;

                        break;
                    }
                    case "5": {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        Drawable drawable = getResources().getDrawable(R.drawable.transformer);
                        Bitmap bitmap = drawableToBitmap(drawable);
                        if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                            if (secNodeSectionId.get(loadFlowDeviceId) != null) {
                                secNodeSectionId.get(loadFlowDeviceId).getPaint().setColor(Color.BLACK);
                            }
                        }
                        if (deviceLocation == 2) {
                            previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 135, 0)));
                        } else if (deviceLocation == 1) {
                            previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 135)));
                        } else {
                            previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 0)));
                        }


                        if (loadFlowOverVoltageDeviceID.containsKey(loadFlowDeviceId) && overVoltageColors != null) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(loadFlowDeviceId) != null) {
                                    secNodeSectionId.get(loadFlowDeviceId).getPaint().setColor(Color.parseColor(overVoltageColors));
                                    loadFlowDeviceId = null;
                                }
                            }
                            if (deviceLocation == 2) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 135, 0)));
                            } else if (deviceLocation == 1) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 0, 135)));
                            } else {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 0, 0)));
                            }
                        }

                        if (loadFlowUnderVoltageDeviceID.containsKey(loadFlowDeviceId) && underVoltageColors != null) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(loadFlowDeviceId) != null) {
                                    secNodeSectionId.get(loadFlowDeviceId).getPaint().setColor(Color.parseColor(underVoltageColors));
                                    loadFlowDeviceId = null;
                                }
                            }
                            if (deviceLocation == 2) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 135, 0)));
                            } else if (deviceLocation == 1) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 0, 135)));
                            } else {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 0, 0)));
                            }

                        }

                        if (loadFlowOverLoadDeviceID.containsKey(loadFlowDeviceId) && overloadColors != null) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(loadFlowDeviceId) != null) {
                                    secNodeSectionId.get(loadFlowDeviceId).getPaint().setColor(Color.parseColor(overloadColors));
                                    loadFlowDeviceId = null;
                                }
                            }
                            if (deviceLocation == 2) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 135, 0)));
                            } else if (deviceLocation == 1) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 0, 135)));
                            } else {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 0, 0)));
                            }

                        }

                        previousLoadFlowDevice = null;
                        loadFlowDeviceType = null;
                        loadFlowDeviceId = null;

                        break;
                    }
                    case "14": {
                        Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "L", 90f, Color.BLACK);
                        if (deviceLocation == 2) {
                            previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 95, 0)));
                        } else if (deviceLocation == 1) {
                            previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 95)));
                        } else {
                            previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 0)));
                        }

                        if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                            if (secNodeSectionId.get(loadFlowDeviceId) != null) {
                                secNodeSectionId.get(loadFlowDeviceId).getPaint().setColor(Color.BLACK);
                            }
                        }

                        if (loadFlowOverVoltageDeviceID.containsKey(loadFlowDeviceId) && overVoltageColors != null) {
                            if (deviceLocation == 2) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 95, 0)));
                            } else if (deviceLocation == 1) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 0, 95)));
                            } else {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 0, 0)));
                            }
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(loadFlowDeviceId) != null) {
                                    secNodeSectionId.get(loadFlowDeviceId).getPaint().setColor(Color.parseColor(overVoltageColors));
                                }
                            }
                        }

                        if (loadFlowUnderVoltageDeviceID.containsKey(loadFlowDeviceId) && underVoltageColors != null) {
                            if (deviceLocation == 2) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 95, 0)));
                            } else if (deviceLocation == 1) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 0, 95)));
                            } else {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 0, 0)));
                            }
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(loadFlowDeviceId) != null) {
                                    secNodeSectionId.get(loadFlowDeviceId).getPaint().setColor(Color.parseColor(underVoltageColors));
                                }
                            }
                        }

                        if (loadFlowOverLoadDeviceID.containsKey(loadFlowDeviceId) && overloadColors != null) {
                            if (deviceLocation == 2) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 95, 0)));
                            } else if (deviceLocation == 1) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 0, 95)));
                            } else {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 0, 0)));
                            }
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(loadFlowDeviceId) != null) {
                                    secNodeSectionId.get(loadFlowDeviceId).getPaint().setColor(Color.parseColor(overloadColors));
                                }
                            }
                        }

                        previousLoadFlowDevice = null;
                        loadFlowDeviceType = null;
                        loadFlowDeviceId = null;
                        break;
                    }
                    case "13": {
                        Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "D", 70f, Color.BLACK);
                        int paddingLeft = 0; // Left padding in pixels

                        int paddingTop = 0; // Top padding in pixels

                        int paddingRight = 0; // Right padding in pixels

                        int paddingBottom = 12; // Bottom padding in pixels

                        int newWidth = bitmap.getWidth() + paddingLeft + paddingRight;
                        int newHeight = bitmap.getHeight() + paddingTop + paddingBottom;
                        Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, bitmap.getConfig());
                        Canvas canva = new Canvas(paddedBitmap);
                        canva.drawBitmap(bitmap, paddingLeft, paddingTop, null);
                        if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                            if (secNodeSectionId.get(loadFlowDeviceId) != null) {
                                secNodeSectionId.get(loadFlowDeviceId).getPaint().setColor(Color.BLACK);
                            }
                        }
                        if (deviceLocation == 2) {
                            previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.BLACK), 95, 0)));
                        } else if (deviceLocation == 1) {
                            previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.BLACK), 0, 95)));
                        } else {
                            previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.BLACK), 0, 0)));
                        }

                        if (loadFlowOverVoltageDeviceID.containsKey(loadFlowDeviceId) && overVoltageColors != null) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(loadFlowDeviceId) != null) {
                                    secNodeSectionId.get(loadFlowDeviceId).getPaint().setColor(Color.parseColor(overVoltageColors));
                                }
                            }
                            if (deviceLocation == 2) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(overVoltageColors)), 95, 0)));
                            } else if (deviceLocation == 1) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(overVoltageColors)), 0, 95)));
                            } else {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(overVoltageColors)), 0, 0)));
                            }
                        }

                        if (loadFlowUnderVoltageDeviceID.containsKey(loadFlowDeviceId) && underVoltageColors != null) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(loadFlowDeviceId) != null) {
                                    secNodeSectionId.get(loadFlowDeviceId).getPaint().setColor(Color.parseColor(underVoltageColors));
                                }
                            }
                            if (deviceLocation == 2) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(underVoltageColors)), 95, 0)));
                            } else if (deviceLocation == 1) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(underVoltageColors)), 0, 95)));
                            } else {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(underVoltageColors)), 0, 0)));
                            }
                        }

                        if (loadFlowOverLoadDeviceID.containsKey(loadFlowDeviceId) && overloadColors != null) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(loadFlowDeviceId) != null) {
                                    secNodeSectionId.get(loadFlowDeviceId).getPaint().setColor(Color.parseColor(overloadColors));
                                    loadFlowDeviceId = null;
                                }
                            }
                            if (deviceLocation == 2) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(overloadColors)), 95, 0)));
                            } else if (deviceLocation == 1) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(overloadColors)), 0, 95)));
                            } else {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(overloadColors)), 0, 0)));
                            }

                        }

                        previousLoadFlowDevice = null;
                        loadFlowDeviceType = null;
                        loadFlowDeviceId = null;
                        break;
                    }
                    case "61":
                        previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.BLACK), 0, 0)));

                        if (!secNodeSectionId.isEmpty()) {
                            if (secNodeSectionId.get(loadFlowDeviceId) != null) {
                                secNodeSectionId.get(loadFlowDeviceId).getPaint().setColor(Color.BLACK);
                            }
                        }

                        if (loadFlowOverVoltageDeviceID.containsKey(loadFlowDeviceId) && overVoltageColors != null) {
                            previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(overVoltageColors)), 0, 0)));

                            if (!secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(loadFlowDeviceId) != null) {
                                    secNodeSectionId.get(loadFlowDeviceId).getPaint().setColor(Color.parseColor(overVoltageColors));
                                }
                            }
                        }

                        if (loadFlowUnderVoltageDeviceID.containsKey(loadFlowDeviceId) && underVoltageColors != null) {
                            previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(underVoltageColors)), 0, 0)));

                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(loadFlowDeviceId) != null) {
                                    secNodeSectionId.get(loadFlowDeviceId).getPaint().setColor(Color.parseColor(underVoltageColors));
                                }
                            }
                        }

                        if (loadFlowOverLoadDeviceID.containsKey(loadFlowDeviceId) && overloadColors != null) {
                            previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(overloadColors)), 0, 0)));

                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(loadFlowDeviceId) != null) {
                                    secNodeSectionId.get(loadFlowDeviceId).getPaint().setColor(Color.parseColor(overloadColors));
                                }
                            }
                        }

                        previousLoadFlowDevice = null;
                        loadFlowDeviceType = null;
                        loadFlowDeviceId = null;

                        break;
                    case "20": {
                        int paddingPx = 0;
                        Bitmap paddedBitmap = Bitmap.createBitmap(BitmapImg.Spotload().getWidth() + paddingPx * 2, // Add padding to both sides
                                BitmapImg.Spotload().getHeight() + paddingPx * 2, Bitmap.Config.ARGB_8888);
                        Canvas canvas1 = new Canvas(paddedBitmap);
                        canvas1.drawBitmap(BitmapImg.Spotload(), paddingPx, 10, null);
                        if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                            if (secNodeSectionId.get(loadFlowDeviceId) != null) {
                                secNodeSectionId.get(loadFlowDeviceId).getPaint().setColor(Color.BLACK);
                            }
                        }
                        if (spLineSectionList.contains(loadFlowDeviceId)) {
                            previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.BLACK), 0, 0)));
                        } else {
                            if (deviceLocation == 2) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.BLACK), 70, 0)));
                            } else if (deviceLocation == 1) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.BLACK), 0, 70)));
                            } else {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.BLACK), 0, 0)));
                            }
                        }

                        if (loadFlowOverVoltageDeviceID.containsKey(loadFlowDeviceId) && overVoltageColors != null) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(loadFlowDeviceId) != null) {
                                    secNodeSectionId.get(loadFlowDeviceId).getPaint().setColor(Color.parseColor(overVoltageColors));
                                }
                            }

                            if (spLineSectionList.contains(loadFlowDeviceId)) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(overVoltageColors)), 0, 0)));
                            } else {
                                if (deviceLocation == 2) {
                                    previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(overVoltageColors)), 70, 0)));
                                } else if (deviceLocation == 1) {
                                    previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(overVoltageColors)), 0, 70)));
                                } else {
                                    previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(overVoltageColors)), 0, 0)));
                                }
                            }

                        }

                        if (loadFlowUnderVoltageDeviceID.containsKey(loadFlowDeviceId) && underVoltageColors != null) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(loadFlowDeviceId) != null) {
                                    secNodeSectionId.get(loadFlowDeviceId).getPaint().setColor(Color.parseColor(underVoltageColors));
                                }
                            }
                            if (spLineSectionList.contains(loadFlowDeviceId)) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(underVoltageColors)), 0, 0)));
                            } else {
                                if (deviceLocation == 2) {
                                    previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(underVoltageColors)), 70, 0)));
                                } else if (deviceLocation == 1) {
                                    previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(underVoltageColors)), 0, 70)));
                                } else {
                                    previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(underVoltageColors)), 0, 0)));
                                }
                            }
                        }

                        if (loadFlowOverLoadDeviceID.containsKey(loadFlowDeviceId) && overloadColors != null) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(loadFlowDeviceId) != null) {
                                    secNodeSectionId.get(loadFlowDeviceId).getPaint().setColor(Color.parseColor(overloadColors));
                                }
                            }
                            if (spLineSectionList.contains(loadFlowDeviceId)) {
                                previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(overloadColors)), 0, 0)));
                            } else {
                                if (deviceLocation == 2) {
                                    previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(overloadColors)), 70, 0)));
                                } else if (deviceLocation == 1) {
                                    previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(overloadColors)), 0, 70)));
                                } else {
                                    previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(overloadColors)), 0, 0)));
                                }
                            }

                        }

                        previousLoadFlowDevice = null;
                        loadFlowDeviceType = null;
                        loadFlowDeviceId = null;

                        break;
                    }
                    case "99": {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        Drawable drawable = getResources().getDrawable(R.drawable.dot);
                        Bitmap bitmap = drawableToBitmap(drawable);
                        previousLoadFlowDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), changeBgTransparentBitmapColor(bitmap, Color.BLACK)));
                        previousLoadFlowDevice = null;
                        loadFlowDeviceType = null;
                        loadFlowDeviceId = null;
                        break;
                    }
                }
            }

            polyline.setColor(Color.parseColor("#0ABDE3"));
            loadFlowPreviousSelectedSection = polyline;
            loadFlowSectionId = sectionId;
        } catch (Exception e) {
            Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
        }
    }

    private void highlightShortCircuitDevice(Marker marker, String type, String ID, int location) {
        try {
            if (shortCircuitPreviousDevice != null && shortCircuitDeviceType != null && shortCircuitDeviceId != null) {
                switch (shortCircuitDeviceType) {
                    case "8": {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        Drawable drawable = getResources().getDrawable(R.drawable.breaker);
                        Bitmap bitmap = drawableToBitmap(drawable);
                        if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                            if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.BLACK);
                            }
                        }
                        if (deviceLocation == 2) {
                            shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 135, 0)));
                        } else if (deviceLocation == 1) {
                            shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 135)));
                        } else {
                            shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 0)));
                        }

                        if (shortCircuitRatingDeviceID.containsKey(shortCircuitDeviceId) && ratingColors != null) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(ratingColors));
                                }
                            }
                            if (deviceLocation == 2) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(ratingColors)), 135, 0)));
                            } else if (deviceLocation == 1) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(ratingColors)), 0, 135)));
                            } else {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(ratingColors)), 0, 0)));
                            }
                        }

                        if (shortCircuitOverVoltageDeviceID.containsKey(shortCircuitDeviceId) && overVoltageColors != null) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(overVoltageColors));
                                }
                            }
                            if (deviceLocation == 2) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 135, 0)));
                            } else if (deviceLocation == 1) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 0, 135)));
                            } else {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 0, 0)));
                            }
                        }

                        if (shortCircuitUnderVoltageDeviceID.containsKey(shortCircuitDeviceId) && underVoltageColors != null) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(underVoltageColors));
                                }
                            }
                            if (deviceLocation == 2) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 135, 0)));
                            } else if (deviceLocation == 1) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 0, 135)));
                            } else {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 0, 0)));
                            }
                        }

                        if (shortCircuitOverLoadDeviceID.containsKey(shortCircuitDeviceId) && overloadColors != null) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(overloadColors));
                                }
                            }
                            if (deviceLocation == 2) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 135, 0)));
                            } else if (deviceLocation == 1) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 0, 135)));
                            } else {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 0, 0)));
                            }
                        }

                        shortCircuitPreviousDevice = null;
                        shortCircuitDeviceType = null;
                        shortCircuitDeviceId = null;

                        break;
                    }
                    case "5": {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        Drawable drawable = getResources().getDrawable(R.drawable.transformer);
                        Bitmap bitmap = drawableToBitmap(drawable);
                        if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                            if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.BLACK);
                            }
                        }
                        if (deviceLocation == 2) {
                            shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 135, 0)));
                        } else if (deviceLocation == 1) {
                            shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 135)));
                        } else {
                            shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 0)));
                        }

                        if (shortCircuitRatingDeviceID.containsKey(shortCircuitDeviceId) && ratingColors != null) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(ratingColors));
                                }
                            }
                            if (deviceLocation == 2) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(ratingColors)), 135, 0)));
                            } else if (deviceLocation == 1) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(ratingColors)), 0, 135)));
                            } else {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(ratingColors)), 0, 0)));
                            }
                        }

                        if (shortCircuitOverVoltageDeviceID.containsKey(shortCircuitDeviceId) && overVoltageColors != null) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(overVoltageColors));
                                }
                            }
                            if (deviceLocation == 2) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 135, 0)));
                            } else if (deviceLocation == 1) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 0, 135)));
                            } else {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 0, 0)));
                            }
                        }

                        if (shortCircuitUnderVoltageDeviceID.containsKey(shortCircuitDeviceId) && underVoltageColors != null) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(underVoltageColors));
                                }
                            }
                            if (deviceLocation == 2) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 135, 0)));
                            } else if (deviceLocation == 1) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 0, 135)));
                            } else {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 0, 0)));
                            }
                        }

                        if (shortCircuitOverLoadDeviceID.containsKey(shortCircuitDeviceId) && overloadColors != null) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(overloadColors));
                                }
                            }
                            if (deviceLocation == 2) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 135, 0)));
                            } else if (deviceLocation == 1) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 0, 135)));
                            } else {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 0, 0)));
                            }
                        }

                        shortCircuitPreviousDevice = null;
                        shortCircuitDeviceType = null;
                        shortCircuitDeviceId = null;

                        break;
                    }
                    case "14": {
                        Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "L", 90f, Color.BLACK);
                        if (deviceLocation == 2) {
                            shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 95, 0)));
                        } else if (deviceLocation == 1) {
                            shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 95)));
                        } else {
                            shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 0)));
                        }
                        if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                            if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.BLACK);
                            }
                        }

                        if (shortCircuitRatingDeviceID.containsKey(shortCircuitDeviceId) && ratingColors != null) {
                            if (deviceLocation == 2) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(ratingColors)), 95, 0)));
                            } else if (deviceLocation == 1) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(ratingColors)), 0, 95)));
                            } else {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(ratingColors)), 0, 0)));
                            }

                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(ratingColors));
                                }
                            }
                        }

                        if (shortCircuitOverVoltageDeviceID.containsKey(shortCircuitDeviceId) && overVoltageColors != null) {
                            if (deviceLocation == 2) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 95, 0)));
                            } else if (deviceLocation == 1) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 0, 95)));
                            } else {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 0, 0)));
                            }

                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(overVoltageColors));
                                }
                            }
                        }

                        if (shortCircuitUnderVoltageDeviceID.containsKey(shortCircuitDeviceId) && underVoltageColors != null) {
                            if (deviceLocation == 2) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 95, 0)));
                            } else if (deviceLocation == 1) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 0, 95)));
                            } else {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 0, 0)));
                            }

                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(underVoltageColors));
                                }
                            }
                        }

                        if (shortCircuitOverLoadDeviceID.containsKey(shortCircuitDeviceId) && overloadColors != null) {
                            if (deviceLocation == 2) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 95, 0)));
                            } else if (deviceLocation == 1) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 0, 95)));
                            } else {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 0, 0)));
                            }
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(overloadColors));
                                }
                            }
                        }

                        shortCircuitPreviousDevice = null;
                        shortCircuitDeviceType = null;
                        shortCircuitDeviceId = null;

                        break;
                    }
                    case "13": {
                        Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "D", 70f, Color.BLACK);
                        int paddingLeft = 0; // Left padding in pixels

                        int paddingTop = 0; // Top padding in pixels

                        int paddingRight = 0; // Right padding in pixels

                        int paddingBottom = 12; // Bottom padding in pixels

                        int newWidth = bitmap.getWidth() + paddingLeft + paddingRight;
                        int newHeight = bitmap.getHeight() + paddingTop + paddingBottom;
                        Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, bitmap.getConfig());
                        Canvas canva = new Canvas(paddedBitmap);
                        canva.drawBitmap(bitmap, paddingLeft, paddingTop, null);
                        if (deviceLocation == 2) {
                            shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.BLACK), 95, 0)));
                        } else if (deviceLocation == 1) {
                            shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.BLACK), 0, 95)));
                        } else {
                            shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.BLACK), 0, 0)));

                        }
                        if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                            if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.BLACK);
                            }
                        }

                        if (shortCircuitRatingDeviceID.containsKey(shortCircuitDeviceId) && ratingColors != null) {
                            if (deviceLocation == 2) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(ratingColors)), 95, 0)));
                            } else if (deviceLocation == 1) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(ratingColors)), 0, 95)));
                            } else {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(ratingColors)), 0, 0)));
                            }
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(ratingColors));
                                }
                            }
                        }

                        if (shortCircuitOverVoltageDeviceID.containsKey(shortCircuitDeviceId) && overVoltageColors != null) {
                            if (deviceLocation == 2) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(overVoltageColors)), 95, 0)));
                            } else if (deviceLocation == 1) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(overVoltageColors)), 0, 95)));
                            } else {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(overVoltageColors)), 0, 0)));
                            }

                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(overVoltageColors));
                                }
                            }
                        }

                        if (shortCircuitUnderVoltageDeviceID.containsKey(shortCircuitDeviceId) && underVoltageColors != null) {
                            if (deviceLocation == 2) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(underVoltageColors)), 95, 0)));
                            } else if (deviceLocation == 1) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(underVoltageColors)), 0, 95)));
                            } else {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(underVoltageColors)), 0, 0)));
                            }
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(underVoltageColors));
                                }
                            }
                        }

                        if (shortCircuitOverLoadDeviceID.containsKey(shortCircuitDeviceId) && overloadColors != null) {
                            if (deviceLocation == 2) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(overloadColors)), 0, 95)));
                            } else if (deviceLocation == 1) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(overloadColors)), 0, 95)));
                            } else {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(overloadColors)), 0, 0)));
                            }
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(overloadColors));
                                }
                            }
                        }

                        shortCircuitPreviousDevice = null;
                        shortCircuitDeviceType = null;
                        shortCircuitDeviceId = null;

                        break;
                    }
                    case "61":
                        shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.BLACK), 0, 0)));


                        if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                            if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.BLACK);
                            }
                        }

                        if (shortCircuitRatingDeviceID.containsKey(shortCircuitDeviceId) && ratingColors != null) {
                            if (secNodeSectionId.containsKey(shortCircuitDeviceId)) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(ratingColors)), 0, 0)));
                            } else {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(ratingColors)), 0, 0)));
                            }

                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(ratingColors));
                                }
                            }
                        }

                        if (shortCircuitOverVoltageDeviceID.containsKey(shortCircuitDeviceId) && overVoltageColors != null) {
                            if (secNodeSectionId.containsKey(shortCircuitDeviceId)) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(overVoltageColors)), 0, 0)));
                            } else {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(overVoltageColors)), 0, 0)));
                            }

                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(overVoltageColors));
                                }
                            }
                        }

                        if (shortCircuitUnderVoltageDeviceID.containsKey(shortCircuitDeviceId) && underVoltageColors != null) {
                            if (secNodeSectionId.containsKey(shortCircuitDeviceId)) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(underVoltageColors)), 0, 0)));
                            } else {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(underVoltageColors)), 0, 0)));
                            }

                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(underVoltageColors));
                                }
                            }
                        }

                        if (shortCircuitOverLoadDeviceID.containsKey(shortCircuitDeviceId) && overloadColors != null) {
                            if (secNodeSectionId.containsKey(shortCircuitDeviceId)) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(overloadColors)), 0, 0)));
                            } else {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(overloadColors)), 0, 0)));
                            }

                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(overloadColors));
                                }
                            }
                        }

                        shortCircuitPreviousDevice = null;
                        shortCircuitDeviceType = null;
                        shortCircuitDeviceId = null;

                        break;
                    case "20": {
                        int paddingPx = 0;
                        Bitmap paddedBitmap = Bitmap.createBitmap(BitmapImg.Spotload().getWidth() + paddingPx * 2, // Add padding to both sides
                                BitmapImg.Spotload().getHeight() + paddingPx * 2, Bitmap.Config.ARGB_8888);
                        Canvas canvas1 = new Canvas(paddedBitmap);
                        canvas1.drawBitmap(BitmapImg.Spotload(), paddingPx, 10, null);
                        if (spLineSectionList.contains(shortCircuitDeviceId)) {
                            shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.BLACK), 0, 0)));
                        } else {
                            if (deviceLocation == 2) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.BLACK), 70, 0)));
                            } else if (deviceLocation == 1) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.BLACK), 0, 70)));
                            } else {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.BLACK), 0, 0)));
                            }
                        }
                        if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                            if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.BLACK);
                            }
                        }

                        if (shortCircuitRatingDeviceID.containsKey(shortCircuitDeviceId) && ratingColors != null) {
                            if (spLineSectionList.contains(shortCircuitDeviceId)) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(ratingColors)), 0, 0)));
                            } else {
                                if (deviceLocation == 2) {
                                    shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(ratingColors)), 70, 0)));
                                } else if (deviceLocation == 1) {
                                    shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(ratingColors)), 0, 70)));
                                } else {
                                    shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(ratingColors)), 0, 0)));
                                }
                            }

                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(ratingColors));
                                }
                            }
                        }

                        if (shortCircuitOverVoltageDeviceID.containsKey(shortCircuitDeviceId) && overVoltageColors != null) {
                            if (spLineSectionList.contains(shortCircuitDeviceId)) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(overVoltageColors)), 0, 0)));
                            } else {
                                if (deviceLocation == 2) {
                                    shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(overVoltageColors)), 70, 0)));
                                } else if (deviceLocation == 1) {
                                    shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(overVoltageColors)), 0, 70)));
                                } else {
                                    shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(overVoltageColors)), 0, 0)));
                                }
                            }
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(overVoltageColors));
                                }
                            }
                        }

                        if (shortCircuitUnderVoltageDeviceID.containsKey(shortCircuitDeviceId) && underVoltageColors != null) {
                            if (spLineSectionList.contains(shortCircuitDeviceId)) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(underVoltageColors)), 0, 0)));
                            } else {
                                if (deviceLocation == 2) {
                                    shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(underVoltageColors)), 70, 0)));
                                } else if (deviceLocation == 1) {
                                    shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(underVoltageColors)), 0, 70)));
                                } else {
                                    shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(underVoltageColors)), 0, 0)));
                                }
                            }
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(underVoltageColors));
                                }
                            }
                        }

                        if (shortCircuitOverLoadDeviceID.containsKey(shortCircuitDeviceId) && overloadColors != null) {
                            if (spLineSectionList.contains(shortCircuitDeviceId)) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(overloadColors)), 0, 0)));
                            } else {
                                if (deviceLocation == 2) {
                                    shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(overloadColors)), 70, 0)));
                                } else if (deviceLocation == 1) {
                                    shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(overloadColors)), 0, 70)));
                                } else {
                                    shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(overloadColors)), 0, 0)));
                                }
                            }
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(overloadColors));
                                }
                            }
                        }

                        shortCircuitPreviousDevice = null;
                        shortCircuitDeviceType = null;
                        shortCircuitDeviceId = null;
                        break;
                    }
                    case "99": {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        Drawable drawable = getResources().getDrawable(R.drawable.dot);
                        Bitmap bitmap = drawableToBitmap(drawable);
                        shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), changeBgTransparentBitmapColor(bitmap, Color.BLACK)));
                        shortCircuitPreviousDevice = null;
                        shortCircuitDeviceType = null;
                        shortCircuitDeviceId = null;
                        break;
                    }
                }
            }

            if (shortCircuitPreviousSelectedSection != null && shortCircuitSectionId != null) {
                shortCircuitPreviousSelectedSection.closeInfoWindow();
                shortCircuitPreviousSelectedSection.setColor(Color.BLACK);

                if (shortCircuitRatingSectionID.containsKey(shortCircuitSectionId) && ratingColors != null) {
                    shortCircuitPreviousSelectedSection.setColor(Color.parseColor(ratingColors));
                }

                if (shortCircuitOverVoltageSectionID.containsKey(shortCircuitSectionId) && overVoltageColors != null) {
                    shortCircuitPreviousSelectedSection.setColor(Color.parseColor(overVoltageColors));
                }

                if (shortCircuitOverLoadSectionID.containsKey(shortCircuitSectionId) && overloadColors != null) {
                    shortCircuitPreviousSelectedSection.setColor(Color.parseColor(overloadColors));
                }

                if (shortCircuitUnderVoltageSectionID.containsKey(shortCircuitSectionId) && underVoltageColors != null) {
                    shortCircuitPreviousSelectedSection.setColor(Color.parseColor(underVoltageColors));
                }
            }

            switch (type) {
                case "8": {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    Drawable drawable = getResources().getDrawable(R.drawable.breaker);
                    Bitmap bitmap = drawableToBitmap(drawable);
                    if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                        if (secNodeSectionId.get(ID) != null) {
                            secNodeSectionId.get(ID).getPaint().setColor(Color.parseColor("#0ABDE3"));
                        }
                    }
                    if (location == 2) {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor("#0ABDE3")), 135, 0)));
                    } else if (location == 1) {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor("#0ABDE3")), 0, 135)));
                    } else {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor("#0ABDE3")), 0, 0)));
                    }
                    break;
                }
                case "5": {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    Drawable drawable = getResources().getDrawable(R.drawable.transformer);
                    Bitmap bitmap = drawableToBitmap(drawable);
                    if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                        if (secNodeSectionId.get(ID) != null) {
                            secNodeSectionId.get(ID).getPaint().setColor(Color.parseColor("#0ABDE3"));
                        }
                    }
                    if (location == 2) {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor("#0ABDE3")), 135, 0)));
                    } else if (location == 1) {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor("#0ABDE3")), 0, 135)));
                    } else {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor("#0ABDE3")), 0, 0)));
                    }
                    break;
                }
                case "14": {
                    if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                        if (secNodeSectionId.get(ID) != null) {
                            secNodeSectionId.get(ID).getPaint().setColor(Color.parseColor("#0ABDE3"));
                        }
                    }
                    Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "L", 90f, Color.BLACK);
                    if (location == 2) {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor("#0ABDE3")), 95, 0)));
                    } else if (location == 1) {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor("#0ABDE3")), 0, 95)));
                    } else {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor("#0ABDE3")), 0, 0)));
                    }
                    break;
                }
                case "13": {
                    Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "D", 70f, Color.BLACK);
                    int paddingLeft = 0; // Left padding in pixels

                    int paddingTop = 0; // Top padding in pixels

                    int paddingRight = 0; // Right padding in pixels

                    int paddingBottom = 12; // Bottom padding in pixels

                    int newWidth = bitmap.getWidth() + paddingLeft + paddingRight;
                    int newHeight = bitmap.getHeight() + paddingTop + paddingBottom;
                    Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, bitmap.getConfig());
                    Canvas canva = new Canvas(paddedBitmap);
                    canva.drawBitmap(bitmap, paddingLeft, paddingTop, null);
                    if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                        if (secNodeSectionId.get(ID) != null) {
                            secNodeSectionId.get(ID).getPaint().setColor(Color.parseColor("#0ABDE3"));
                        }
                    }
                    if (location == 2) {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor("#0ABDE3")), 95, 0)));
                    } else if (location == 1) {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor("#0ABDE3")), 0, 95)));
                    } else {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor("#0ABDE3")), 0, 0)));
                    }
                    break;
                }
                case "61":
                    if (secNodeSectionId.containsKey(ID)) {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor("#0ABDE3")), 0, 0)));
                    } else {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor("#0ABDE3")), 0, 0)));
                    }

                    if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                        if (secNodeSectionId.get(ID) != null) {
                            secNodeSectionId.get(ID).getPaint().setColor(Color.parseColor("#0ABDE3"));
                        }
                    }
                    break;
                case "20": {
                    int paddingPx = 0;
                    Bitmap paddedBitmap = Bitmap.createBitmap(BitmapImg.Spotload().getWidth() + paddingPx * 2, // Add padding to both sides
                            BitmapImg.Spotload().getHeight() + paddingPx * 2, Bitmap.Config.ARGB_8888);
                    Canvas canvas1 = new Canvas(paddedBitmap);
                    canvas1.drawBitmap(BitmapImg.Spotload(), paddingPx, 10, null);
                    if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                        if (secNodeSectionId.get(ID) != null) {
                            secNodeSectionId.get(ID).getPaint().setColor(Color.parseColor("#0ABDE3"));
                        }
                    }
                    if (spLineSectionList.contains(ID)) {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor("#0ABDE3")), 0, 0)));
                    } else {
                        if (location == 2) {
                            marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor("#0ABDE3")), 70, 0)));
                        } else if (location == 1) {
                            marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor("#0ABDE3")), 0, 70)));
                        } else {
                            marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor("#0ABDE3")), 0, 0)));
                        }
                    }
                    break;
                }
                case "99": {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    Drawable drawable = getResources().getDrawable(R.drawable.node);
                    Bitmap bitmap = drawableToBitmap(drawable);
                    marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), changeBgTransparentBitmapColor(bitmap, Color.parseColor("#0ABDE3"))));
                    break;
                }
            }

            shortCircuitPreviousDevice = marker;
            shortCircuitDeviceType = type;
            shortCircuitDeviceId = ID;
        } catch (Exception e) {
            Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
        }
    }

    private void highlightShortCircuitSection(Polyline polyline, String sectionId, int location) {
        try {
            if (shortCircuitPreviousSelectedSection != null && shortCircuitSectionId != null) {
                shortCircuitPreviousSelectedSection.closeInfoWindow();
                shortCircuitPreviousSelectedSection.setColor(Color.BLACK);

                if (shortCircuitRatingSectionID.containsKey(shortCircuitSectionId) && ratingColors != null) {
                    shortCircuitPreviousSelectedSection.setColor(Color.parseColor(ratingColors));
                }

                if (shortCircuitOverVoltageSectionID.containsKey(shortCircuitSectionId) && overVoltageColors != null) {
                    shortCircuitPreviousSelectedSection.setColor(Color.parseColor(overVoltageColors));
                }

                if (shortCircuitOverLoadSectionID.containsKey(shortCircuitSectionId) && overloadColors != null) {
                    shortCircuitPreviousSelectedSection.setColor(Color.parseColor(overloadColors));
                }

                if (shortCircuitUnderVoltageSectionID.containsKey(shortCircuitSectionId) && underVoltageColors != null) {
                    shortCircuitPreviousSelectedSection.setColor(Color.parseColor(underVoltageColors));
                }
            }

            if (shortCircuitPreviousDevice != null && shortCircuitDeviceType != null && shortCircuitDeviceId != null) {
                switch (shortCircuitDeviceType) {
                    case "8": {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        Drawable drawable = getResources().getDrawable(R.drawable.breaker);
                        Bitmap bitmap = drawableToBitmap(drawable);
                        if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                            if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.BLACK);
                            }
                        }
                        if (deviceLocation == 2) {
                            shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 135, 0)));
                        } else if (deviceLocation == 1) {
                            shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 135)));
                        } else {
                            shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 0)));
                        }

                        if (shortCircuitRatingDeviceID.containsKey(shortCircuitDeviceId) && ratingColors != null) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(ratingColors));
                                }
                            }
                            if (deviceLocation == 2) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(ratingColors)), 135, 0)));
                            } else if (deviceLocation == 1) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(ratingColors)), 0, 135)));
                            } else {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(ratingColors)), 0, 0)));
                            }
                        }

                        if (shortCircuitOverVoltageDeviceID.containsKey(shortCircuitDeviceId) && overVoltageColors != null) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(overVoltageColors));
                                }
                            }
                            if (deviceLocation == 2) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 135, 0)));
                            } else if (deviceLocation == 1) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 0, 135)));
                            } else {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 0, 0)));
                            }
                        }

                        if (shortCircuitUnderVoltageDeviceID.containsKey(shortCircuitDeviceId) && underVoltageColors != null) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(underVoltageColors));
                                }
                            }
                            if (deviceLocation == 2) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 135, 0)));
                            } else if (deviceLocation == 1) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 0, 135)));
                            } else {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 0, 0)));
                            }
                        }

                        if (shortCircuitOverLoadDeviceID.containsKey(shortCircuitDeviceId) && overloadColors != null) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(overloadColors));
                                }
                            }
                            if (deviceLocation == 2) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 135, 0)));
                            } else if (deviceLocation == 1) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 0, 135)));
                            } else {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 0, 0)));
                            }
                        }

                        shortCircuitPreviousDevice = null;
                        shortCircuitDeviceType = null;
                        shortCircuitDeviceId = null;

                        break;
                    }
                    case "5": {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        Drawable drawable = getResources().getDrawable(R.drawable.transformer);
                        Bitmap bitmap = drawableToBitmap(drawable);
                        if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                            if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.BLACK);
                            }
                        }
                        if (deviceLocation == 2) {
                            shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 135, 0)));
                        } else if (deviceLocation == 1) {
                            shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 135)));
                        } else {
                            shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 0)));
                        }

                        if (shortCircuitRatingDeviceID.containsKey(shortCircuitDeviceId) && ratingColors != null) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(ratingColors));
                                }
                            }
                            if (deviceLocation == 2) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(ratingColors)), 135, 0)));
                            } else if (deviceLocation == 1) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(ratingColors)), 0, 135)));
                            } else {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(ratingColors)), 0, 0)));
                            }
                        }

                        if (shortCircuitOverVoltageDeviceID.containsKey(shortCircuitDeviceId) && overVoltageColors != null) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(overVoltageColors));
                                }
                            }
                            if (deviceLocation == 2) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 135, 0)));
                            } else if (deviceLocation == 1) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 0, 135)));
                            } else {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 0, 0)));
                            }
                        }

                        if (shortCircuitUnderVoltageDeviceID.containsKey(shortCircuitDeviceId) && underVoltageColors != null) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(underVoltageColors));
                                }
                            }
                            if (deviceLocation == 2) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 135, 0)));
                            } else if (deviceLocation == 1) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 0, 135)));
                            } else {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 0, 0)));
                            }
                        }

                        if (shortCircuitOverLoadDeviceID.containsKey(shortCircuitDeviceId) && overloadColors != null) {
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(overloadColors));
                                }
                            }
                            if (deviceLocation == 2) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 135, 0)));
                            } else if (deviceLocation == 1) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 0, 135)));
                            } else {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 0, 0)));
                            }
                        }

                        shortCircuitPreviousDevice = null;
                        shortCircuitDeviceType = null;
                        shortCircuitDeviceId = null;

                        break;
                    }
                    case "14": {
                        Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "L", 90f, Color.BLACK);
                        if (deviceLocation == 2) {
                            shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 95, 0)));
                        } else if (deviceLocation == 1) {
                            shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 95)));
                        } else {
                            shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 0)));
                        }
                        if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                            if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.BLACK);
                            }
                        }

                        if (shortCircuitRatingDeviceID.containsKey(shortCircuitDeviceId) && ratingColors != null) {
                            if (deviceLocation == 2) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(ratingColors)), 95, 0)));
                            } else if (deviceLocation == 1) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(ratingColors)), 0, 95)));
                            } else {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(ratingColors)), 0, 0)));
                            }

                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(ratingColors));
                                }
                            }
                        }

                        if (shortCircuitOverVoltageDeviceID.containsKey(shortCircuitDeviceId) && overVoltageColors != null) {
                            if (deviceLocation == 2) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 95, 0)));
                            } else if (deviceLocation == 1) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 0, 95)));
                            } else {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 0, 0)));
                            }

                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(overVoltageColors));
                                }
                            }
                        }

                        if (shortCircuitUnderVoltageDeviceID.containsKey(shortCircuitDeviceId) && underVoltageColors != null) {
                            if (deviceLocation == 2) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 95, 0)));
                            } else if (deviceLocation == 1) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 0, 95)));
                            } else {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 0, 0)));
                            }

                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(underVoltageColors));
                                }
                            }
                        }

                        if (shortCircuitOverLoadDeviceID.containsKey(shortCircuitDeviceId) && overloadColors != null) {
                            if (deviceLocation == 2) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 95, 0)));
                            } else if (deviceLocation == 1) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 0, 95)));
                            } else {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 0, 0)));
                            }
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(overloadColors));
                                }
                            }
                        }

                        shortCircuitPreviousDevice = null;
                        shortCircuitDeviceType = null;
                        shortCircuitDeviceId = null;

                        break;
                    }
                    case "13": {
                        Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "D", 70f, Color.BLACK);
                        int paddingLeft = 0; // Left padding in pixels

                        int paddingTop = 0; // Top padding in pixels

                        int paddingRight = 0; // Right padding in pixels

                        int paddingBottom = 12; // Bottom padding in pixels

                        int newWidth = bitmap.getWidth() + paddingLeft + paddingRight;
                        int newHeight = bitmap.getHeight() + paddingTop + paddingBottom;
                        Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, bitmap.getConfig());
                        Canvas canva = new Canvas(paddedBitmap);
                        canva.drawBitmap(bitmap, paddingLeft, paddingTop, null);
                        if (deviceLocation == 2) {
                            shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.BLACK), 95, 0)));
                        } else if (deviceLocation == 1) {
                            shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.BLACK), 0, 95)));
                        } else {
                            shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.BLACK), 0, 0)));

                        }
                        if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                            if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.BLACK);
                            }
                        }

                        if (shortCircuitRatingDeviceID.containsKey(shortCircuitDeviceId) && ratingColors != null) {
                            if (deviceLocation == 2) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(ratingColors)), 95, 0)));
                            } else if (deviceLocation == 1) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(ratingColors)), 0, 95)));
                            } else {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(ratingColors)), 0, 0)));
                            }
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(ratingColors));
                                }
                            }
                        }

                        if (shortCircuitOverVoltageDeviceID.containsKey(shortCircuitDeviceId) && overVoltageColors != null) {
                            if (deviceLocation == 2) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(overVoltageColors)), 95, 0)));
                            } else if (deviceLocation == 1) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(overVoltageColors)), 0, 95)));
                            } else {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(overVoltageColors)), 0, 0)));
                            }

                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(overVoltageColors));
                                }
                            }
                        }

                        if (shortCircuitUnderVoltageDeviceID.containsKey(shortCircuitDeviceId) && underVoltageColors != null) {
                            if (deviceLocation == 2) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(underVoltageColors)), 95, 0)));
                            } else if (deviceLocation == 1) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(underVoltageColors)), 0, 95)));
                            } else {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(underVoltageColors)), 0, 0)));
                            }
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(underVoltageColors));
                                }
                            }
                        }

                        if (shortCircuitOverLoadDeviceID.containsKey(shortCircuitDeviceId) && overloadColors != null) {
                            if (deviceLocation == 2) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(overloadColors)), 0, 95)));
                            } else if (deviceLocation == 1) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(overloadColors)), 0, 95)));
                            } else {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(overloadColors)), 0, 0)));
                            }
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(overloadColors));
                                }
                            }
                        }

                        shortCircuitPreviousDevice = null;
                        shortCircuitDeviceType = null;
                        shortCircuitDeviceId = null;

                        break;
                    }
                    case "61":
                        shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.BLACK), 0, 0)));


                        if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                            if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.BLACK);
                            }
                        }

                        if (shortCircuitRatingDeviceID.containsKey(shortCircuitDeviceId) && ratingColors != null) {
                            if (secNodeSectionId.containsKey(shortCircuitDeviceId)) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(ratingColors)), 0, 0)));
                            } else {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(ratingColors)), 0, 0)));
                            }

                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(ratingColors));
                                }
                            }
                        }

                        if (shortCircuitOverVoltageDeviceID.containsKey(shortCircuitDeviceId) && overVoltageColors != null) {
                            if (secNodeSectionId.containsKey(shortCircuitDeviceId)) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(overVoltageColors)), 0, 0)));
                            } else {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(overVoltageColors)), 0, 0)));
                            }

                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(overVoltageColors));
                                }
                            }
                        }

                        if (shortCircuitUnderVoltageDeviceID.containsKey(shortCircuitDeviceId) && underVoltageColors != null) {
                            if (secNodeSectionId.containsKey(shortCircuitDeviceId)) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(underVoltageColors)), 0, 0)));
                            } else {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(underVoltageColors)), 0, 0)));
                            }

                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(underVoltageColors));
                                }
                            }
                        }

                        if (shortCircuitOverLoadDeviceID.containsKey(shortCircuitDeviceId) && overloadColors != null) {
                            if (secNodeSectionId.containsKey(shortCircuitDeviceId)) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(overloadColors)), 0, 0)));
                            } else {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(overloadColors)), 0, 0)));
                            }

                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(overloadColors));
                                }
                            }
                        }

                        shortCircuitPreviousDevice = null;
                        shortCircuitDeviceType = null;
                        shortCircuitDeviceId = null;

                        break;
                    case "20": {
                        int paddingPx = 0;
                        Bitmap paddedBitmap = Bitmap.createBitmap(BitmapImg.Spotload().getWidth() + paddingPx * 2, // Add padding to both sides
                                BitmapImg.Spotload().getHeight() + paddingPx * 2, Bitmap.Config.ARGB_8888);
                        Canvas canvas1 = new Canvas(paddedBitmap);
                        canvas1.drawBitmap(BitmapImg.Spotload(), paddingPx, 10, null);
                        if (spLineSectionList.contains(shortCircuitDeviceId)) {
                            shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.BLACK), 0, 0)));
                        } else {
                            if (deviceLocation == 2) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.BLACK), 70, 0)));
                            } else if (deviceLocation == 1) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.BLACK), 0, 70)));
                            } else {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.BLACK), 0, 0)));
                            }
                        }
                        if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                            if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.BLACK);
                            }
                        }

                        if (shortCircuitRatingDeviceID.containsKey(shortCircuitDeviceId) && ratingColors != null) {
                            if (spLineSectionList.contains(shortCircuitDeviceId)) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(ratingColors)), 0, 0)));
                            } else {
                                if (deviceLocation == 2) {
                                    shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(ratingColors)), 70, 0)));
                                } else if (deviceLocation == 1) {
                                    shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(ratingColors)), 0, 70)));
                                } else {
                                    shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(ratingColors)), 0, 0)));
                                }
                            }

                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(ratingColors));
                                }
                            }
                        }

                        if (shortCircuitOverVoltageDeviceID.containsKey(shortCircuitDeviceId) && overVoltageColors != null) {
                            if (spLineSectionList.contains(shortCircuitDeviceId)) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(overVoltageColors)), 0, 0)));
                            } else {
                                if (deviceLocation == 2) {
                                    shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(overVoltageColors)), 70, 0)));
                                } else if (deviceLocation == 1) {
                                    shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(overVoltageColors)), 0, 70)));
                                } else {
                                    shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(overVoltageColors)), 0, 0)));
                                }
                            }
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(overVoltageColors));
                                }
                            }
                        }

                        if (shortCircuitUnderVoltageDeviceID.containsKey(shortCircuitDeviceId) && underVoltageColors != null) {
                            if (spLineSectionList.contains(shortCircuitDeviceId)) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(underVoltageColors)), 0, 0)));
                            } else {
                                if (deviceLocation == 2) {
                                    shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(underVoltageColors)), 70, 0)));
                                } else if (deviceLocation == 1) {
                                    shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(underVoltageColors)), 0, 70)));
                                } else {
                                    shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(underVoltageColors)), 0, 0)));
                                }
                            }
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(underVoltageColors));
                                }
                            }
                        }

                        if (shortCircuitOverLoadDeviceID.containsKey(shortCircuitDeviceId) && overloadColors != null) {
                            if (spLineSectionList.contains(shortCircuitDeviceId)) {
                                shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(overloadColors)), 0, 0)));
                            } else {
                                if (deviceLocation == 2) {
                                    shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(overloadColors)), 70, 0)));
                                } else if (deviceLocation == 1) {
                                    shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(overloadColors)), 0, 70)));
                                } else {
                                    shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(overloadColors)), 0, 0)));
                                }
                            }
                            if (secNodeSectionId != null && !secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(shortCircuitDeviceId) != null) {
                                    secNodeSectionId.get(shortCircuitDeviceId).getPaint().setColor(Color.parseColor(overloadColors));
                                }
                            }
                        }

                        shortCircuitPreviousDevice = null;
                        shortCircuitDeviceType = null;
                        shortCircuitDeviceId = null;
                        break;
                    }
                    case "99": {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        Drawable drawable = getResources().getDrawable(R.drawable.dot);
                        Bitmap bitmap = drawableToBitmap(drawable);
                        shortCircuitPreviousDevice.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), changeBgTransparentBitmapColor(bitmap, Color.BLACK)));
                        shortCircuitPreviousDevice = null;
                        shortCircuitDeviceType = null;
                        shortCircuitDeviceId = null;
                        break;
                    }
                }
            }

            polyline.setColor(Color.parseColor("#0ABDE3"));
            shortCircuitPreviousSelectedSection = polyline;
            shortCircuitSectionId = sectionId;
        } catch (Exception e) {
            Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
        }
    }

    private void showEnabledLocationDialog() {
        new AlertDialog.Builder(MapActivity.this)
                .setTitle("Enable Location")
                .setMessage("Location services are required for this feature. Please enable GPS.")
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void drawRoute(Location currentLocation) {
        if (currentLocation == null) return;  // Ensure location is available
        // ✅ Set Start and Destination points
        GeoPoint startPoint = new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
        GeoPoint destinationPoint = new GeoPoint(37.7749, -122.4194); // Example: San Francisco
        // ✅ Initialize RoadManager (GraphHopper or OSRM recommended for real use)
        RoadManager roadManager = new OSRMRoadManager(this, "YourUserAgent"); // Use your API key if needed
        ArrayList<GeoPoint> waypoints = new ArrayList<>();
        waypoints.add(startPoint);
        waypoints.add(destinationPoint);
        // ✅ Get the road (route)
        Road road = roadManager.getRoad(waypoints);
        // ✅ Create Polyline to show the road
        Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
        roadOverlay.setColor(Color.BLUE);  // Set route color
        roadOverlay.setWidth(10.0f);  // Set route thickness
        // ✅ Add route overlay to the map
        binding.map.getOverlays().add(roadOverlay);
        binding.map.invalidate();
        // ✅ Move map to start of route
        binding.map.getController().animateTo(startPoint, 18.0, 1000L);
    }

    @SuppressLint("StaticFieldLeak")
    private class AddGeoJsonData extends AsyncTask<Void, Void, FolderOverlay> {
        private final MapView mMapView;
        private final JSONObject jsonObject;

        public AddGeoJsonData(MapView mMapView, JSONObject jsonObject) {
            this.mMapView = mMapView;
            this.jsonObject = jsonObject;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBarLayout.setVisibility(View.VISIBLE);
            progressBarLayout.setProcessText("Loading Networks...");
        }

        @Override
        protected FolderOverlay doInBackground(Void... voids) {
            try {
                if (!jsonObject.toString().isEmpty() && !jsonObject.toString().trim().equals("null")) {
                    JSONObject object = new JSONObject();
                    object = jsonObject.getJSONObject("output");
                    if (!object.getJSONObject("cables_data2").getJSONArray("features").toString().equals("[]") || !object.getJSONObject("oh_data2").getJSONArray("features").toString().equals("[]") || !object.getJSONObject("oh_data2").getJSONArray("features").toString().equals("[]") || !object.getJSONObject("HEADNODE").getJSONArray("features").toString().equals("[]")) {

                        if (!object.getJSONObject("HEADNODE").getJSONArray("features").toString().equals("[]")) {
                            if (sourceKml != null) {
                                sourceKml.parseGeoJSON(object.getJSONObject("HEADNODE").toString());
                                KmlFeature.Styler styler = new SourceKmlStyler(Color.BLUE, binding.map);
                                FolderOverlay folderOverlay = (FolderOverlay) sourceKml.mKmlRoot.buildOverlay(binding.map, null, styler, sourceKml);
                                SourceOverLay.add(folderOverlay);
                                double x = Double.parseDouble(object.getJSONObject("HEADNODE").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("fromy"));
                                double y = Double.parseDouble(object.getJSONObject("HEADNODE").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("fromX"));
                                sourcePoint = new GeoPoint(x, y);

                                for (int i = 0; i < continentList.size(); i++) {
                                    if (continentList.get(i).getName().contains("Feeder ID")) {
                                        for (int j = 0; j < object.getJSONObject("HEADNODE").getJSONArray("features").length(); j++) {
                                            DType type = new DType(object.getJSONObject("HEADNODE").getJSONArray("features").getJSONObject(j).getJSONObject("properties").getString("NetworkId"), 43);
                                            continentList.get(i).getDeviceList().add(type);
                                            continentList.get(i).setName("Feeder ID" + " " + "(" + continentList.get(i).getDeviceList().size() + ")");
                                        }
                                    }
                                }
                            } else {
                                sourceKml = new KmlDocument();
                                sourceKml.parseGeoJSON(object.getJSONObject("HEADNODE").toString());
                                KmlFeature.Styler styler = new SourceKmlStyler(Color.BLUE, binding.map);
                                SourceOverLay = (FolderOverlay) sourceKml.mKmlRoot.buildOverlay(binding.map, null, styler, sourceKml);

                                double x = Double.parseDouble(object.getJSONObject("HEADNODE").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("fromy"));
                                double y = Double.parseDouble(object.getJSONObject("HEADNODE").getJSONArray("features").getJSONObject(0).getJSONObject("properties").getString("fromX"));
                                sourcePoint = new GeoPoint(x, y);

                                ArrayList<DType> list = new ArrayList<>();
                                DType type;
                                for (int i = 0; i < object.getJSONObject("HEADNODE").getJSONArray("features").length(); i++) {
                                    type = new DType(object.getJSONObject("HEADNODE").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("NetworkId"), 43);
                                    list.add(type);
                                    DeviceName deviceName = new DeviceName(object.getJSONObject("HEADNODE").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("NetworkId"), "43");
                                    mList.add(deviceName);
                                }
                                Continent continent = new Continent("Feeder ID" + " " + "(" + list.size() + ")", list);
                                continentList.add(continent);
                            }
                        }

                        if (!object.getJSONObject("cables_data2").getJSONArray("features").toString().equals("[]")) {
                            if (CableKml != null) {
                                CableKml.parseGeoJSON(object.getJSONObject("cables_data2").toString());
                                KmlFeature.Styler styler = new MyKmlStyler(Color.RED, binding.map);
                                FolderOverlay folderOverlay = (FolderOverlay) CableKml.mKmlRoot.buildOverlay(binding.map, null, styler, CableKml);
                                CableFolderOverLay.add(folderOverlay);

                                for (int i = 0; i < continentList.size(); i++) {
                                    if (continentList.get(i).getName().contains("Cable")) {
                                        for (int j = 0; j < object.getJSONObject("cables_data2").getJSONArray("features").length(); j++) {
                                            DType type = new DType(object.getJSONObject("cables_data2").getJSONArray("features").getJSONObject(j).getJSONObject("properties").getString("DeviceNumber"), Integer.parseInt(object.getJSONObject("cables_data2").getJSONArray("features").getJSONObject(j).getJSONObject("properties").getString("DeviceType")));
                                            continentList.get(i).getDeviceList().add(type);
                                            continentList.get(i).setName("Cable" + " " + "(" + continentList.get(i).getDeviceList().size() + ")");
                                            caSectionList.add(object.getJSONObject("cables_data2").getJSONArray("features").getJSONObject(j).getJSONObject("properties").getString("SectionId"));
                                        }
                                    }
                                }

                                for (int j = 0; j < object.getJSONObject("cables_data2").getJSONArray("features").length(); j++) {
                                    CaObject.getJSONArray("features").put(object.getJSONObject("cables_data2").getJSONArray("features").get(j));
                                }
                            } else {
                                CaObject = new JSONObject(object.getJSONObject("cables_data2").toString());
                                CableKml = new KmlDocument();
                                CableKml.parseGeoJSON(object.getJSONObject("cables_data2").toString());
                                KmlFeature.Styler styler = new MyKmlStyler(Color.RED, binding.map);
                                CableFolderOverLay = (FolderOverlay) CableKml.mKmlRoot.buildOverlay(binding.map, null, styler, CableKml);

                                ArrayList<DType> list = new ArrayList<>();
                                DType type;
                                DeviceName deviceName;
                                for (int i = 0; i < object.getJSONObject("cables_data2").getJSONArray("features").length(); i++) {
                                    type = new DType(object.getJSONObject("cables_data2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceNumber"), Integer.parseInt(object.getJSONObject("cables_data2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceType")));
                                    list.add(type);
                                    deviceName = new DeviceName(object.getJSONObject("cables_data2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceNumber"), object.getJSONObject("cables_data2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceType"));
                                    mList.add(deviceName);
                                    caSectionList.add(object.getJSONObject("cables_data2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("SectionId"));
                                }
                                Continent continent = new Continent("Cable" + " " + "(" + list.size() + ")", list);
                                continentList.add(continent);
                            }
                        }

                        if (!object.getJSONObject("oh_data2").getJSONArray("features").toString().equals("[]")) {
                            if (OverHeadKml != null) {
                                OverHeadKml.parseGeoJSON(object.getJSONObject("oh_data2").toString());
                                KmlFeature.Styler styler = new OverHeadKmlStyler(Color.BLUE, binding.map);
                                FolderOverlay folderOverlay = (FolderOverlay) OverHeadKml.mKmlRoot.buildOverlay(binding.map, null, styler, OverHeadKml);
                                OverheadFolderOverLay.add(folderOverlay);

                                for (int i = 0; i < continentList.size(); i++) {
                                    if (continentList.get(i).getName().contains("Overhead Balance")) {
                                        for (int j = 0; j < object.getJSONObject("oh_data2").getJSONArray("features").length(); j++) {
                                            DType type = new DType(object.getJSONObject("oh_data2").getJSONArray("features").getJSONObject(j).getJSONObject("properties").getString("SectionId"), Integer.parseInt(object.getJSONObject("oh_data2").getJSONArray("features").getJSONObject(j).getJSONObject("properties").getString("DeviceType")));
                                            continentList.get(i).getDeviceList().add(type);
                                            continentList.get(i).setName("Overhead Balance" + " " + "(" + continentList.get(i).getDeviceList().size() + ")");
                                        }
                                    }
                                }

                                for (int j = 0; j < object.getJSONObject("oh_data2").getJSONArray("features").length(); j++) {
                                    OhObject.getJSONArray("features").put(object.getJSONObject("oh_data2").getJSONArray("features").get(j));
                                }

                            } else {
                                OhObject = new JSONObject(object.getJSONObject("oh_data2").toString());
                                OverHeadKml = new KmlDocument();
                                OverHeadKml.parseGeoJSON(object.getJSONObject("oh_data2").toString());
                                KmlFeature.Styler styler = new OverHeadKmlStyler(Color.BLUE, binding.map);
                                OverheadFolderOverLay = (FolderOverlay) OverHeadKml.mKmlRoot.buildOverlay(binding.map, null, styler, OverHeadKml);

                                ArrayList<DType> list = new ArrayList<>();
                                DType type;
                                for (int i = 0; i < object.getJSONObject("oh_data2").getJSONArray("features").length(); i++) {
                                    type = new DType(object.getJSONObject("oh_data2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceNumber"), Integer.parseInt(object.getJSONObject("oh_data2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceType")));
                                    list.add(type);
                                    DeviceName deviceName = new DeviceName(object.getJSONObject("oh_data2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceNumber"), object.getJSONObject("oh_data2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceType"));
                                    mList.add(deviceName);
                                    //OhSectionList.add(OhObject.getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("SectionId"));
                                }
                                Continent continent = new Continent("Overhead Balance" + " " + "(" + list.size() + ")", list);
                                continentList.add(continent);
                            }

                        }

                        if (!object.getJSONObject("ohunbal_data2").getJSONArray("features").toString().equals("[]")) {
                            if (UnBalencedKml != null) {
                                UnBalencedKml.parseGeoJSON(object.getJSONObject("ohunbal_data2").toString());
                                KmlFeature.Styler styler = new UnbalanceKmlStyler(Color.BLACK, binding.map);
                                FolderOverlay folderOverlay = (FolderOverlay) UnBalencedKml.mKmlRoot.buildOverlay(binding.map, null, styler, UnBalencedKml);
                                UnBalanceFolderOverLay.add(folderOverlay);

                                for (int i = 0; i < continentList.size(); i++) {
                                    if (continentList.get(i).getName().contains("Unbalance")) {
                                        for (int j = 0; j < object.getJSONObject("ohunbal_data2").getJSONArray("features").length(); j++) {
                                            DType type = new DType(object.getJSONObject("ohunbal_data2").getJSONArray("features").getJSONObject(j).getJSONObject("properties").getString("SectionId"), Integer.parseInt(object.getJSONObject("ohunbal_data2").getJSONArray("features").getJSONObject(j).getJSONObject("properties").getString("DeviceType")));
                                            continentList.get(i).getDeviceList().add(type);
                                            continentList.get(i).setName("Unbalance" + " " + "(" + continentList.get(i).getDeviceList().size() + ")");
                                        }
                                    }
                                }

                                for (int j = 0; j < object.getJSONObject("ohunbal_data2").getJSONArray("features").length(); j++) {
                                    UnBalObject.getJSONArray("features").put(object.getJSONObject("ohunbal_data2").getJSONArray("features").get(j));
                                }

                            } else {
                                UnBalObject = new JSONObject(object.getJSONObject("ohunbal_data2").toString());
                                UnBalencedKml = new KmlDocument();
                                UnBalencedKml.parseGeoJSON(object.getJSONObject("ohunbal_data2").toString());
                                KmlFeature.Styler styler = new UnbalanceKmlStyler(Color.BLACK, binding.map);
                                UnBalanceFolderOverLay = (FolderOverlay) UnBalencedKml.mKmlRoot.buildOverlay(binding.map, null, styler, UnBalencedKml);

                                ArrayList<DType> list = new ArrayList<>();
                                DType type;
                                for (int i = 0; i < object.getJSONObject("ohunbal_data2").getJSONArray("features").length(); i++) {
                                    type = new DType(object.getJSONObject("ohunbal_data2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceNumber"), Integer.parseInt(object.getJSONObject("ohunbal_data2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceType")));
                                    list.add(type);
                                    DeviceName deviceName = new DeviceName(object.getJSONObject("ohunbal_data2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceNumber"), object.getJSONObject("ohunbal_data2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceType"));
                                    mList.add(deviceName);
                                }
                                Continent continent = new Continent("Unbalance" + " " + "(" + list.size() + ")", list);
                                continentList.add(continent);
                            }
                        }

                        if (!object.getJSONObject("sectionnodev").getJSONArray("features").toString().equals("[]")) {
                            if (sectionNodeKml != null) {
                                sectionNodeKml.parseGeoJSON(String.valueOf(object.getJSONObject("sectionnodev")));
                                KmlFeature.Styler styler = new SectionNodeKmlStyler(Color.BLACK, binding.map);

                                FolderOverlay folderOverlay = (FolderOverlay) sectionNodeKml.mKmlRoot.buildOverlay(binding.map, null, styler, sectionNodeKml);
                                sectionFolderOverLay.add(folderOverlay);
                                for (int j = 0; j < object.getJSONObject("sectionnodev").getJSONArray("features").length(); j++) {
                                    SecNodeObject.getJSONArray("features").put(object.getJSONObject("sectionnodev").getJSONArray("features").get(j));
                                }

                                for (int i = 0; i < SecNodeObject.getJSONArray("features").length(); i++) {
                                    spLineSectionList.add(SecNodeObject.getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceNumber"));
                                }
                            } else {
                                SecNodeObject = new JSONObject(object.getJSONObject("sectionnodev").toString());
                                sectionNodeKml = new KmlDocument();
                                sectionNodeKml.parseGeoJSON(String.valueOf(SecNodeObject));
                                KmlFeature.Styler styler = new SectionNodeKmlStyler(Color.BLACK, binding.map);
                                sectionFolderOverLay = (FolderOverlay) sectionNodeKml.mKmlRoot.buildOverlay(binding.map, null, styler, sectionNodeKml);
                                for (int i = 0; i < SecNodeObject.getJSONArray("features").length(); i++) {
                                    spLineSectionList.add(SecNodeObject.getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceNumber"));
                                }
                            }
                        }

                        if (!object.getJSONObject("cb_data2").getJSONArray("features").toString().equals("[]")) {
                            if (BreakarKml != null) {
                                BreakarKml.parseGeoJSON(object.getJSONObject("cb_data2").toString());
                                KmlFeature.Styler styler = new circuitBreakerKmlStyler(Color.BLUE, binding.map);
                                FolderOverlay folderOverlay = (FolderOverlay) BreakarKml.mKmlRoot.buildOverlay(binding.map, null, styler, BreakarKml);
                                CircuitBreakerOverLay.add(folderOverlay);

                                for (int i = 0; i < continentList.size(); i++) {
                                    if (continentList.get(i).getName().contains("Breaker")) {
                                        for (int j = 0; j < object.getJSONObject("cb_data2").getJSONArray("features").length(); j++) {
                                            DType type = new DType(object.getJSONObject("cb_data2").getJSONArray("features").getJSONObject(j).getJSONObject("properties").getString("DeviceNumber"), Integer.parseInt(object.getJSONObject("cb_data2").getJSONArray("features").getJSONObject(j).getJSONObject("properties").getString("DeviceType")));
                                            continentList.get(i).getDeviceList().add(type);
                                            continentList.get(i).setName("Breaker" + " " + "(" + continentList.get(i).getDeviceList().size() + ")");
                                        }

                                    }
                                }
                            } else {
                                BreakarKml = new KmlDocument();
                                BreakarKml.parseGeoJSON(object.getJSONObject("cb_data2").toString());
                                KmlFeature.Styler styler = new circuitBreakerKmlStyler(Color.BLUE, binding.map);
                                CircuitBreakerOverLay = (FolderOverlay) BreakarKml.mKmlRoot.buildOverlay(binding.map, null, styler, BreakarKml);

                                ArrayList<DType> list = new ArrayList<>();
                                DType type;
                                for (int i = 0; i < object.getJSONObject("cb_data2").getJSONArray("features").length(); i++) {
                                    type = new DType(object.getJSONObject("cb_data2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceNumber"), Integer.parseInt(object.getJSONObject("cb_data2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceType")));
                                    list.add(type);
                                    DeviceName deviceName = new DeviceName(object.getJSONObject("cb_data2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceNumber"), object.getJSONObject("cb_data2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceType"));
                                    mList.add(deviceName);
                                }
                                Continent continent = new Continent("Breaker" + " " + "(" + list.size() + ")", list);
                                continentList.add(continent);
                            }
                        }

                        if (!object.getJSONObject("dt_data2").getJSONArray("features").toString().equals("[]")) {
                            if (TransformerKml != null) {
                                TransformerKml.parseGeoJSON(object.getJSONObject("dt_data2").toString());
                                KmlFeature.Styler styler = new DistributionTransferKmlStyler(Color.BLUE, binding.map);
                                FolderOverlay folderOverlay = (FolderOverlay) TransformerKml.mKmlRoot.buildOverlay(binding.map, null, styler, TransformerKml);
                                DistributionTransferOverLay.add(folderOverlay);

                                for (int i = 0; i < continentList.size(); i++) {
                                    if (continentList.get(i).getName().contains("Two-Winding Transformer")) {
                                        for (int j = 0; j < object.getJSONObject("dt_data2").getJSONArray("features").length(); j++) {
                                            if (object.getJSONObject("dt_data2").getJSONArray("features").getJSONObject(j).getJSONObject("properties").getString("DTCName") != null && !object.getJSONObject("dt_data2").getJSONArray("features").getJSONObject(j).getJSONObject("properties").getString("DTCName").isEmpty() && !object.getJSONObject("dt_data2").getJSONArray("features").getJSONObject(j).getJSONObject("properties").getString("DTCName").equals("null")) {
                                                DType type = new DType(object.getJSONObject("dt_data2").getJSONArray("features").getJSONObject(j).getJSONObject("properties").getString("DeviceNumber") + (object.getJSONObject("dt_data2").getJSONArray("features").getJSONObject(j).getJSONObject("properties").getString("DTCName")), Integer.parseInt(object.getJSONObject("dt_data2").getJSONArray("features").getJSONObject(j).getJSONObject("properties").getString("DeviceType")));
                                                continentList.get(i).getDeviceList().add(type);
                                                continentList.get(i).setName("Two-Winding Transformer" + " " + "(" + continentList.get(i).getDeviceList().size() + ")");
                                            } else {
                                                DType type = new DType(object.getJSONObject("dt_data2").getJSONArray("features").getJSONObject(j).getJSONObject("properties").getString("DeviceNumber"), Integer.parseInt(object.getJSONObject("dt_data2").getJSONArray("features").getJSONObject(j).getJSONObject("properties").getString("DeviceType")));
                                                continentList.get(i).getDeviceList().add(type);
                                                continentList.get(i).setName("Two-Winding Transformer" + " " + "(" + continentList.get(i).getDeviceList().size() + ")");
                                            }
                                        }
                                    }
                                }
                            } else {
                                TransformerKml = new KmlDocument();
                                TransformerKml.parseGeoJSON(object.getJSONObject("dt_data2").toString());
                                KmlFeature.Styler styler = new DistributionTransferKmlStyler(Color.BLUE, binding.map);
                                DistributionTransferOverLay = (FolderOverlay) TransformerKml.mKmlRoot.buildOverlay(binding.map, null, styler, TransformerKml);

                                ArrayList<DType> list = new ArrayList<>();
                                DType type;
                                for (int i = 0; i < object.getJSONObject("dt_data2").getJSONArray("features").length(); i++) {

                                    if (object.getJSONObject("dt_data2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DTCName") != null && !object.getJSONObject("dt_data2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DTCName").isEmpty() && !object.getJSONObject("dt_data2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DTCName").equals("null")) {
                                        type = new DType(object.getJSONObject("dt_data2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceNumber") + " (" + object.getJSONObject("dt_data2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DTCName") + ") ", Integer.parseInt(object.getJSONObject("dt_data2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceType")));
                                        list.add(type);
                                        DeviceName deviceName = new DeviceName(object.getJSONObject("dt_data2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceNumber"), object.getJSONObject("dt_data2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceType"));
                                        mList.add(deviceName);
                                    } else {
                                        type = new DType(object.getJSONObject("dt_data2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceNumber"), Integer.parseInt(object.getJSONObject("dt_data2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceType")));
                                        list.add(type);
                                        DeviceName deviceName = new DeviceName(object.getJSONObject("dt_data2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceNumber"), object.getJSONObject("dt_data2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceType"));
                                        mList.add(deviceName);
                                    }
                                }
                                Continent continent = new Continent("Two-Winding Transformer" + " " + "(" + list.size() + ")", list);
                                continentList.add(continent);
                            }

                        }

                        if (!object.getJSONObject("fuse_data2").getJSONArray("features").toString().equals("[]")) {
                            if (FuseKml != null) {
                                FuseKml.parseGeoJSON(object.getJSONObject("fuse_data2").toString());
                                KmlFeature.Styler styler = new FuseKmlStyler(Color.BLUE, binding.map);
                                FolderOverlay folderOverlay = (FolderOverlay) FuseKml.mKmlRoot.buildOverlay(binding.map, null, styler, FuseKml);
                                FuseOverLay.add(folderOverlay);

                                for (int i = 0; i < continentList.size(); i++) {
                                    if (continentList.get(i).getName().contains("Fuse")) {
                                        for (int j = 0; j < object.getJSONObject("fuse_data2").getJSONArray("features").length(); j++) {
                                            DType type = new DType(object.getJSONObject("fuse_data2").getJSONArray("features").getJSONObject(j).getJSONObject("properties").getString("DeviceNumber"), Integer.parseInt(object.getJSONObject("fuse_data2").getJSONArray("features").getJSONObject(j).getJSONObject("properties").getString("DeviceType")));
                                            continentList.get(i).getDeviceList().add(type);
                                            continentList.get(i).setName("Fuse" + " " + "(" + continentList.get(i).getDeviceList().size() + ")");
                                        }
                                    }
                                }
                            } else {
                                FuseKml = new KmlDocument();
                                FuseKml.parseGeoJSON(object.getJSONObject("fuse_data2").toString());
                                KmlFeature.Styler styler = new FuseKmlStyler(Color.BLUE, binding.map);
                                FuseOverLay = (FolderOverlay) FuseKml.mKmlRoot.buildOverlay(binding.map, null, styler, FuseKml);

                                ArrayList<DType> list = new ArrayList<>();
                                DType type;
                                for (int i = 0; i < object.getJSONObject("fuse_data2").getJSONArray("features").length(); i++) {
                                    type = new DType(object.getJSONObject("fuse_data2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceNumber"), Integer.parseInt(object.getJSONObject("fuse_data2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceType")));
                                    list.add(type);
                                    DeviceName deviceName = new DeviceName(object.getJSONObject("fuse_data2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceNumber"), object.getJSONObject("fuse_data2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceType"));
                                    mList.add(deviceName);
                                }
                                Continent continent = new Continent("Fuse" + " " + "(" + list.size() + ")", list);
                                continentList.add(continent);
                            }

                        }

                        if (!object.getJSONObject("switch_data2").getJSONArray("features").toString().equals("[]")) {
                            if (SwitchKml != null) {
                                SwitchKml.parseGeoJSON(object.getJSONObject("switch_data2").toString());
                                KmlFeature.Styler styler = new SwitchKmlStyler(Color.BLACK, binding.map);
                                FolderOverlay folderOverlay = (FolderOverlay) SwitchKml.mKmlRoot.buildOverlay(binding.map, null, styler, SwitchKml);
                                SwitchOverLay.add(folderOverlay);

                                for (int i = 0; i < continentList.size(); i++) {
                                    if (continentList.get(i).getName().contains("Switch")) {
                                        for (int j = 0; j < object.getJSONObject("switch_data2").getJSONArray("features").length(); j++) {
                                            DType type = new DType(object.getJSONObject("switch_data2").getJSONArray("features").getJSONObject(j).getJSONObject("properties").getString("DeviceNumber"), Integer.parseInt(object.getJSONObject("switch_data2").getJSONArray("features").getJSONObject(j).getJSONObject("properties").getString("DeviceType")));
                                            continentList.get(i).getDeviceList().add(type);
                                            continentList.get(i).setName("Switch" + " " + "(" + continentList.get(i).getDeviceList().size() + ")");
                                        }
                                    }
                                }
                            } else {
                                SwitchKml = new KmlDocument();
                                SwitchKml.parseGeoJSON(object.getJSONObject("switch_data2").toString());
                                KmlFeature.Styler styler = new SwitchKmlStyler(Color.BLACK, binding.map);
                                SwitchOverLay = (FolderOverlay) SwitchKml.mKmlRoot.buildOverlay(binding.map, null, styler, SwitchKml);

                                ArrayList<DType> list = new ArrayList<>();
                                DType type;
                                for (int i = 0; i < object.getJSONObject("switch_data2").getJSONArray("features").length(); i++) {
                                    type = new DType(object.getJSONObject("switch_data2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceNumber"), Integer.parseInt(object.getJSONObject("switch_data2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceType")));
                                    list.add(type);
                                    DeviceName deviceName = new DeviceName(object.getJSONObject("switch_data2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceNumber"), object.getJSONObject("switch_data2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceType"));
                                    mList.add(deviceName);
                                }
                                Continent continent = new Continent("Switch" + " " + "(" + list.size() + ")", list);
                                continentList.add(continent);
                            }

                        }

                        if (!object.getJSONObject("shunt_capacitor2").getJSONArray("features").toString().equals("[]")) {
                            if (ShuntCapacitorKml != null) {
                                ShuntCapacitorKml.parseGeoJSON(object.getJSONObject("shunt_capacitor2").toString());
                                KmlFeature.Styler styler = new ShuntCapacitorKmlStyler(Color.BLUE, binding.map);
                                FolderOverlay folderOverlay = (FolderOverlay) ShuntCapacitorKml.mKmlRoot.buildOverlay(binding.map, null, styler, ShuntCapacitorKml);
                                ShuntCapacitorOverLay.add(folderOverlay);

                                for (int i = 0; i < continentList.size(); i++) {
                                    if (continentList.get(i).getName().contains("Shunt Capacitor")) {
                                        for (int j = 0; j < object.getJSONObject("shunt_capacitor2").getJSONArray("features").length(); j++) {
                                            DType type = new DType(object.getJSONObject("shunt_capacitor2").getJSONArray("features").getJSONObject(j).getJSONObject("properties").getString("DeviceNumber"), Integer.parseInt(object.getJSONObject("shunt_capacitor2").getJSONArray("features").getJSONObject(j).getJSONObject("properties").getString("DeviceType")));
                                            continentList.get(i).getDeviceList().add(type);
                                            continentList.get(i).setName("Shunt Capacitor" + " " + "(" + continentList.get(i).getDeviceList().size() + ")");
                                        }

                                    }
                                }
                            } else {
                                ShuntCapacitorKml = new KmlDocument();
                                ShuntCapacitorKml.parseGeoJSON(object.getJSONObject("shunt_capacitor2").toString());
                                KmlFeature.Styler styler = new ShuntCapacitorKmlStyler(Color.BLUE, binding.map);
                                ShuntCapacitorOverLay = (FolderOverlay) ShuntCapacitorKml.mKmlRoot.buildOverlay(binding.map, null, styler, ShuntCapacitorKml);

                                ArrayList<DType> list = new ArrayList<>();
                                DType type;
                                for (int i = 0; i < object.getJSONObject("shunt_capacitor2").getJSONArray("features").length(); i++) {
                                    type = new DType(object.getJSONObject("shunt_capacitor2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceNumber"), Integer.parseInt(object.getJSONObject("shunt_capacitor2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceType")));
                                    list.add(type);
                                    DeviceName deviceName = new DeviceName(object.getJSONObject("shunt_capacitor2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceNumber"), object.getJSONObject("shunt_capacitor2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceType"));
                                    mList.add(deviceName);
                                }
                                Continent continent = new Continent("Shunt Capacitor" + " " + "(" + list.size() + ")", list);
                                continentList.add(continent);
                            }
                        }

                        if (!object.getJSONObject("spotload2").getJSONArray("features").toString().equals("[]")) {
                            if (SpotloadKml != null) {
                                SpotloadKml.parseGeoJSON(object.getJSONObject("spotload2").toString());
                                KmlFeature.Styler styler = new SpotLoadKmlStyler(Color.BLUE, binding.map);
                                FolderOverlay folderOverlay = (FolderOverlay) SpotloadKml.mKmlRoot.buildOverlay(binding.map, null, styler, SpotloadKml);
                                SpotLoadOverLay.add(folderOverlay);

                                for (int i = 0; i < continentList.size(); i++) {
                                    if (continentList.get(i).getName().contains("SpotLoad")) {
                                        for (int j = 0; j < object.getJSONObject("spotload2").getJSONArray("features").length(); j++) {
                                            DType type = new DType(object.getJSONObject("spotload2").getJSONArray("features").getJSONObject(j).getJSONObject("properties").getString("DeviceNumber"), Integer.parseInt(object.getJSONObject("spotload2").getJSONArray("features").getJSONObject(j).getJSONObject("properties").getString("DeviceType")));
                                            continentList.get(i).getDeviceList().add(type);
                                            continentList.get(i).setName("SpotLoad" + " " + "(" + continentList.get(i).getDeviceList().size() + ")");
                                        }
                                    }
                                }
                            } else {
                                SpotloadKml = new KmlDocument();
                                SpotloadKml.parseGeoJSON(object.getJSONObject("spotload2").toString());
                                KmlFeature.Styler styler = new SpotLoadKmlStyler(Color.BLUE, binding.map);
                                SpotLoadOverLay = (FolderOverlay) SpotloadKml.mKmlRoot.buildOverlay(binding.map, null, styler, SpotloadKml);

                                ArrayList<DType> list = new ArrayList<>();
                                DType type;
                                for (int i = 0; i < object.getJSONObject("spotload2").getJSONArray("features").length(); i++) {
                                    type = new DType(object.getJSONObject("spotload2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceNumber"), Integer.parseInt(object.getJSONObject("spotload2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceType")));
                                    list.add(type);
                                    DeviceName deviceName = new DeviceName(object.getJSONObject("spotload2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceNumber"), object.getJSONObject("spotload2").getJSONArray("features").getJSONObject(i).getJSONObject("properties").getString("DeviceType"));
                                    mList.add(deviceName);
                                }
                                Continent continent = new Continent("SpotLoad" + " " + "(" + list.size() + ")", list);
                                continentList.add(continent);
                            }
                        }

                        if (!object.getJSONObject("node_data2").getJSONArray("features").toString().equals("[]")) {
                            if (NodeKml != null) {
                                NodeKml.parseGeoJSON(object.getJSONObject("node_data2").toString());
                                KmlFeature.Styler styler = new NodeKmlStyler(Color.BLACK, binding.map);
                                FolderOverlay folderOverlay = (FolderOverlay) NodeKml.mKmlRoot.buildOverlay(binding.map, null, styler, NodeKml);
                                nodeOverLay.add(folderOverlay);
                            } else {
                                NodeKml = new KmlDocument();
                                NodeKml.parseGeoJSON(object.getJSONObject("node_data2").toString());
                                KmlFeature.Styler styler = new NodeKmlStyler(Color.BLACK, binding.map);
                                nodeOverLay = (FolderOverlay) NodeKml.mKmlRoot.buildOverlay(binding.map, null, styler, NodeKml);
                            }
                        }

                    }
                }
            } catch (Exception e) {
                Log.d("Exception", e.getLocalizedMessage());
            }
            if (CableFolderOverLay != null) {
                return CableFolderOverLay;
            } else if (OverheadFolderOverLay != null) {
                return OverheadFolderOverLay;
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(FolderOverlay folderOverlay) {
            super.onPostExecute(folderOverlay);
            progressBarLayout.setVisibility(View.GONE);

            if (CableFolderOverLay != null) {
                binding.map.getOverlays().add(CableFolderOverLay);
            }

            if (OverheadFolderOverLay != null) {
                binding.map.getOverlays().add(OverheadFolderOverLay);
            }

            if (UnBalanceFolderOverLay != null) {
                binding.map.getOverlays().add(UnBalanceFolderOverLay);
            }

            if (sectionFolderOverLay != null) {
                binding.map.getOverlays().add(sectionFolderOverLay);
            }

            if (SourceOverLay != null) {
                binding.map.getOverlays().add(SourceOverLay);
            }

            if (mList != null && !mList.isEmpty()) {
                FiltersAdapter adapters = new FiltersAdapter(MapActivity.this, R.layout.activity_map, R.id.first_tv, mList);
                binding.searchView.setAdapter(adapters);
            }

            if (continentList != null && !continentList.isEmpty()) {
                adapter = new ExpandableDeviceAdapter(MapActivity.this, continentList);
                binding.navigationmenu.setAdapter(adapter);
            }

            if (!isBounding) {
                if (CableKml != null) {
                    BoundingBox boundingBox = CableKml.mKmlRoot.getBoundingBox();
                    mMapView.zoomToBoundingBox(boundingBox, true);
                    mMapView.getBoundingBox().getCenter();
                    mMapView.getController().setZoom(7.0);
                    mMapView.getController().setCenter(boundingBox.getCenter());
                    mMapView.zoomToBoundingBox(boundingBox.increaseByScale(1.3f), true);
                    isBounding = true;
                    mMapView.invalidate();
                } else if (OverHeadKml != null) {
                    BoundingBox boundingBox = OverHeadKml.mKmlRoot.getBoundingBox();
                    mMapView.zoomToBoundingBox(boundingBox, true);
                    mMapView.getBoundingBox().getCenter();
                    mMapView.getController().setZoom(7.0);
                    mMapView.getController().setCenter(boundingBox.getCenter());
                    mMapView.zoomToBoundingBox(boundingBox.increaseByScale(1.3f), true);
                    isBounding = true;
                    mMapView.invalidate();
                } else {
                    binding.map.getController().animateTo(sourcePoint, 23.0, 0L);
                    binding.map.getController().setCenter(sourcePoint);
                    binding.map.invalidate();
                }
            } else {
                binding.map.invalidate();
            }

            if (!selectedFeeder.isEmpty()) {
                getNetworkData(selectedFeeder.get(0));
            }

            if (!Objects.requireNonNull(intent.getStringExtra("Type")).contains("ExistNetwork")) {
                if (intent.getStringArrayListExtra("NetworkId") != null) {
                    JsonArray jsonArray = new Gson().toJsonTree(intent.getStringArrayListExtra("NetworkId")).getAsJsonArray();
                    if (!isTopology) {
                        getTopology(jsonArray);
                        isTopology = true;
                    }

                }
            }

            if (intent.getStringExtra("NearstConsumerNo") != null && !Objects.requireNonNull(intent.getStringExtra("NearstConsumerNo")).isEmpty() && intent.getStringExtra("DeviceType") != null && !Objects.requireNonNull(intent.getStringExtra("DeviceType")).isEmpty()) {
                getDevices(intent.getStringExtra("NearstConsumerNo"), "20");
            }

            if (Objects.requireNonNull(intent.getStringExtra("Type")).equals("AddNetwork")) {
                binding.sideLLayout.setVisibility(View.GONE);
                binding.colorCodeView.setVisibility(View.GONE);
                binding.menuLayout.setVisibility(View.GONE);
                binding.surveyMenuLayout.setVisibility(View.VISIBLE);
                binding.surveyLLayout.setVisibility(View.VISIBLE);
                binding.locateLocation.setVisibility(View.VISIBLE);
                binding.addSource.setVisibility(View.VISIBLE);
            } else if (Objects.requireNonNull(intent.getStringExtra("Type")).equals("ExistNetwork")) {
                binding.sideLLayout.setVisibility(View.GONE);
                binding.colorCodeView.setVisibility(View.GONE);
                binding.menuLayout.setVisibility(View.GONE);
                binding.addSource.setVisibility(View.VISIBLE);

                binding.surveyMenuLayout.setVisibility(View.VISIBLE);
                binding.surveyLLayout.setVisibility(View.VISIBLE);
                binding.locateLocation.setVisibility(View.VISIBLE);
            } else {
                binding.sideLLayout.setVisibility(View.VISIBLE);
                binding.colorCodeView.setVisibility(View.VISIBLE);
                binding.menuLayout.setVisibility(View.VISIBLE);
                binding.surveyMenuLayout.setVisibility(View.GONE);
                binding.surveyLLayout.setVisibility(View.GONE);
                binding.locateLocation.setVisibility(View.GONE);
                binding.newConnectionLoadFlowStatusImgBtn.setVisibility(View.GONE);
                binding.deleteBtn.setVisibility(View.GONE);
            }

        }

    }

    @SuppressLint("StaticFieldLeak")
    private class LoadFlowAnalysis extends AsyncTask<Void, Void, String> {

        private final LoadFlowModel loadFlowModel;

        public LoadFlowAnalysis(LoadFlowModel loadFlowModel) {
            this.loadFlowModel = loadFlowModel;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBarLayout.setVisibility(View.VISIBLE);
        }

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(Void... voids) {
            String Status = null;
            try {
                Status = "";
                if (loadFlowModel.getOutput().getOverloadColor() != null) {
                    overloadColors = loadFlowModel.getOutput().getOverloadColor();
                }

                if (loadFlowModel.getOutput().getOverVoltageColor() != null) {
                    overVoltageColors = loadFlowModel.getOutput().getOverVoltageColor();
                }

                if (loadFlowModel.getOutput().getUndervoltageColor() != null) {
                    underVoltageColors = loadFlowModel.getOutput().getUndervoltageColor();
                }

                Config.isLoadFlow = true;
                Config.isShortCircuit = false;
                Config.isLoadAllocation = false;

                if (loadFlowModel.getOutput().getOverVoltage() != null && !loadFlowModel.getOutput().getOverVoltage().isEmpty()) {
                    Config.isLoadFlow = true;
                    Config.isShortCircuit = false;
                    Config.isLoadAllocation = false;
                    Status = "Load Flow Complete!";
                    for (int i = 0; i < loadFlowModel.getOutput().getOverVoltage().size(); i++) {
                        loadFlowOverVoltageSectionId.add(loadFlowModel.getOutput().getOverVoltage().get(i).getId());
                        if (CaSectionId.get(loadFlowModel.getOutput().getOverVoltage().get(i).getId()) != null) {
                            Objects.requireNonNull(CaSectionId.get(loadFlowModel.getOutput().getOverVoltage().get(i).getId())).getPaint().setColor(Color.parseColor(loadFlowModel.getOutput().getOverVoltageColor()));
                            loadFlowOverVoltageSectionID.put(loadFlowModel.getOutput().getOverVoltage().get(i).getId(), CaSectionId.get(loadFlowModel.getOutput().getOverVoltage().get(i).getId()));
                        }

                        if (OhSectionId.get(loadFlowModel.getOutput().getOverVoltage().get(i).getId()) != null) {
                            Objects.requireNonNull(OhSectionId.get(loadFlowModel.getOutput().getOverVoltage().get(i).getId())).getPaint().setColor(Color.parseColor(loadFlowModel.getOutput().getOverVoltageColor()));
                            loadFlowOverVoltageSectionID.put(loadFlowModel.getOutput().getOverVoltage().get(i).getId(), OhSectionId.get(loadFlowModel.getOutput().getOverVoltage().get(i).getId()));
                        }

                        if (UnBalSectionId.get(loadFlowModel.getOutput().getOverVoltage().get(i).getId()) != null) {
                            Objects.requireNonNull(UnBalSectionId.get(loadFlowModel.getOutput().getOverVoltage().get(i).getId())).getPaint().setColor(Color.parseColor(loadFlowModel.getOutput().getOverVoltageColor()));
                            loadFlowOverVoltageSectionID.put(loadFlowModel.getOutput().getOverVoltage().get(i).getId(), UnBalSectionId.get(loadFlowModel.getOutput().getOverVoltage().get(i).getId()));
                        }

                        if (breakerSectionId.get(loadFlowModel.getOutput().getOverVoltage().get(i).getId()) != null) {
                            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.breaker);
                            Bitmap bitmap = drawableToBitmap(drawable);
                            Objects.requireNonNull(breakerSectionId.get(loadFlowModel.getOutput().getOverVoltage().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(loadFlowModel.getOutput().getOverVoltageColor())), 0, 0)));
                            loadFlowOverVoltageDeviceID.put(loadFlowModel.getOutput().getOverVoltage().get(i).getId(), breakerSectionId.get(loadFlowModel.getOutput().getOverVoltage().get(i).getId()));
                        }

                        if (transformerSectionId.get(loadFlowModel.getOutput().getOverVoltage().get(i).getId()) != null) {
                            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.transformer);
                            Bitmap bitmap = drawableToBitmap(drawable);
                            Objects.requireNonNull(transformerSectionId.get(loadFlowModel.getOutput().getOverVoltage().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(loadFlowModel.getOutput().getOverVoltageColor())), 93, 0)));
                            loadFlowOverVoltageDeviceID.put(loadFlowModel.getOutput().getOverVoltage().get(i).getId(), transformerSectionId.get(loadFlowModel.getOutput().getOverVoltage().get(i).getId()));
                        }

                        if (fuseSectionId.get(loadFlowModel.getOutput().getOverVoltage().get(i).getId()) != null) {
                            Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "L", 90f, Color.BLACK);
                            Objects.requireNonNull(fuseSectionId.get(loadFlowModel.getOutput().getOverVoltage().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(loadFlowModel.getOutput().getOverVoltageColor())), 0, 0)));
                            loadFlowOverVoltageDeviceID.put(loadFlowModel.getOutput().getOverVoltage().get(i).getId(), fuseSectionId.get(loadFlowModel.getOutput().getOverVoltage().get(i).getId()));
                        }

                        if (switchSectionId.get(loadFlowModel.getOutput().getOverVoltage().get(i).getId()) != null) {
                            Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "D", 70f, Color.BLACK);
                            int paddingLeft = 0; // Left padding in pixels
                            int paddingTop = 0; // Top padding in pixels
                            int paddingRight = 0; // Right padding in pixels
                            int paddingBottom = 12; // Bottom padding in pixels
                            int newWidth = bitmap.getWidth() + paddingLeft + paddingRight;
                            int newHeight = bitmap.getHeight() + paddingTop + paddingBottom;
                            Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, bitmap.getConfig());
                            Canvas canva = new Canvas(paddedBitmap);
                            canva.drawBitmap(bitmap, paddingLeft, paddingTop, null);
                            Objects.requireNonNull(switchSectionId.get(loadFlowModel.getOutput().getOverVoltage().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(loadFlowModel.getOutput().getOverVoltageColor())), 0, 0)));
                            loadFlowOverVoltageDeviceID.put(loadFlowModel.getOutput().getOverVoltage().get(i).getId(), switchSectionId.get(loadFlowModel.getOutput().getOverVoltage().get(i).getId()));
                        }

                        if (capacitorSectionId.get(loadFlowModel.getOutput().getOverVoltage().get(i).getId()) != null) {
                            Objects.requireNonNull(capacitorSectionId.get(loadFlowModel.getOutput().getOverVoltage().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(loadFlowModel.getOutput().getOverVoltageColor())), 90, 0)));
                            loadFlowOverVoltageDeviceID.put(loadFlowModel.getOutput().getOverVoltage().get(i).getId(), capacitorSectionId.get(loadFlowModel.getOutput().getOverVoltage().get(i).getId()));
                        }

                        if (spotLoadSectionId.get(loadFlowModel.getOutput().getOverVoltage().get(i).getId()) != null) {
                            if (spLineSectionList.contains(loadFlowModel.getOutput().getOverVoltage().get(i).getId())) {
                                int paddingPx = 0;
                                Bitmap paddedBitmap = Bitmap.createBitmap(BitmapImg.Spotload().getWidth() + paddingPx * 2, // Add padding to both sides
                                        BitmapImg.Spotload().getHeight() + paddingPx * 2, Bitmap.Config.ARGB_8888);
                                Canvas canvas1 = new Canvas(paddedBitmap);
                                canvas1.drawBitmap(BitmapImg.Spotload(), paddingPx, 10, null);
                                Objects.requireNonNull(spotLoadSectionId.get(loadFlowModel.getOutput().getOverVoltage().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(loadFlowModel.getOutput().getOverVoltageColor())), 0, 0)));
                                loadFlowOverVoltageDeviceID.put(loadFlowModel.getOutput().getOverVoltage().get(i).getId(), spotLoadSectionId.get(loadFlowModel.getOutput().getOverVoltage().get(i).getId()));
                            } else {
                                Objects.requireNonNull(spotLoadSectionId.get(loadFlowModel.getOutput().getOverVoltage().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Spotload(), Color.parseColor(loadFlowModel.getOutput().getOverVoltageColor())), 0, 35)));
                                loadFlowOverVoltageDeviceID.put(loadFlowModel.getOutput().getOverVoltage().get(i).getId(), spotLoadSectionId.get(loadFlowModel.getOutput().getOverVoltage().get(i).getId()));
                            }
                        }

                        if (!secNodeSectionId.isEmpty()) {
                            if (secNodeSectionId.get(loadFlowModel.getOutput().getOverVoltage().get(i).getId()) != null) {
                                Objects.requireNonNull(secNodeSectionId.get(loadFlowModel.getOutput().getOverVoltage().get(i).getId())).getPaint().setColor(Color.parseColor(loadFlowModel.getOutput().getOverVoltageColor()));
                                loadFlowOverVoltageSectionID.put(loadFlowModel.getOutput().getOverVoltage().get(i).getId(), secNodeSectionId.get(loadFlowModel.getOutput().getOverVoltage().get(i).getId()));
                            }
                        }
                    }
                } else {
                    Status = "No Data";
                }

                if (loadFlowModel.getOutput().getUndervoltage() != null && !loadFlowModel.getOutput().getUndervoltage().isEmpty()) {
                    Config.isLoadFlow = true;
                    Config.isShortCircuit = false;
                    Config.isLoadAllocation = false;
                    Status = "Load Flow Complete!";
                    for (int i = 0; i < loadFlowModel.getOutput().getUndervoltage().size(); i++) {
                        loadFlowUnderVoltageSectionId.add(loadFlowModel.getOutput().getUndervoltage().get(i).getId());
                        if (CaSectionId.get(loadFlowModel.getOutput().getUndervoltage().get(i).getId()) != null) {
                            Objects.requireNonNull(CaSectionId.get(loadFlowModel.getOutput().getUndervoltage().get(i).getId())).getPaint().setColor(Color.parseColor(loadFlowModel.getOutput().getUndervoltageColor()));
                            loadFlowUnderVoltageSectionID.put(loadFlowModel.getOutput().getUndervoltage().get(i).getId(), CaSectionId.get(loadFlowModel.getOutput().getUndervoltage().get(i).getId()));
                        }

                        if (OhSectionId.get(loadFlowModel.getOutput().getUndervoltage().get(i).getId()) != null) {
                            Objects.requireNonNull(OhSectionId.get(loadFlowModel.getOutput().getUndervoltage().get(i).getId())).getPaint().setColor(Color.parseColor(loadFlowModel.getOutput().getUndervoltageColor()));
                            loadFlowUnderVoltageSectionID.put(loadFlowModel.getOutput().getUndervoltage().get(i).getId(), OhSectionId.get(loadFlowModel.getOutput().getUndervoltage().get(i).getId()));
                        }

                        if (UnBalSectionId.get(loadFlowModel.getOutput().getUndervoltage().get(i).getId()) != null) {
                            Objects.requireNonNull(UnBalSectionId.get(loadFlowModel.getOutput().getUndervoltage().get(i).getId())).getPaint().setColor(Color.parseColor(loadFlowModel.getOutput().getUndervoltageColor()));
                            loadFlowUnderVoltageSectionID.put(loadFlowModel.getOutput().getUndervoltage().get(i).getId(), UnBalSectionId.get(loadFlowModel.getOutput().getUndervoltage().get(i).getId()));
                        }

                        if (breakerSectionId.get(loadFlowModel.getOutput().getUndervoltage().get(i).getId()) != null) {
                            @SuppressLint("UseCompatLoadingForDrawables")
                            Drawable drawable = getResources().getDrawable(R.drawable.breaker);
                            Bitmap bitmap = drawableToBitmap(drawable);
                            Objects.requireNonNull(breakerSectionId.get(loadFlowModel.getOutput().getUndervoltage().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(loadFlowModel.getOutput().getUndervoltageColor())), 0, 0)));
                            loadFlowUnderVoltageDeviceID.put(loadFlowModel.getOutput().getUndervoltage().get(i).getId(), breakerSectionId.get(loadFlowModel.getOutput().getUndervoltage().get(i).getId()));
                        }

                        if (transformerSectionId.get(loadFlowModel.getOutput().getUndervoltage().get(i).getId()) != null) {
                            @SuppressLint("UseCompatLoadingForDrawables")
                            Drawable drawable = getResources().getDrawable(R.drawable.transformer);
                            Bitmap bitmap = drawableToBitmap(drawable);
                            Objects.requireNonNull(transformerSectionId.get(loadFlowModel.getOutput().getUndervoltage().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(loadFlowModel.getOutput().getUndervoltageColor())), 93, 0)));
                            loadFlowUnderVoltageDeviceID.put(loadFlowModel.getOutput().getUndervoltage().get(i).getId(), transformerSectionId.get(loadFlowModel.getOutput().getUndervoltage().get(i).getId()));
                        }

                        if (fuseSectionId.get(loadFlowModel.getOutput().getUndervoltage().get(i).getId()) != null) {
                            Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "L", 90f, Color.BLACK);
                            Objects.requireNonNull(fuseSectionId.get(loadFlowModel.getOutput().getUndervoltage().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(loadFlowModel.getOutput().getUndervoltageColor())), 0, 0)));
                            loadFlowUnderVoltageDeviceID.put(loadFlowModel.getOutput().getUndervoltage().get(i).getId(), fuseSectionId.get(loadFlowModel.getOutput().getUndervoltage().get(i).getId()));
                        }

                        if (switchSectionId.get(loadFlowModel.getOutput().getUndervoltage().get(i).getId()) != null) {
                            Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "D", 70f, Color.BLACK);
                            int paddingLeft = 0; // Left padding in pixels
                            int paddingTop = 0; // Top padding in pixels
                            int paddingRight = 0; // Right padding in pixels
                            int paddingBottom = 12; // Bottom padding in pixels
                            int newWidth = bitmap.getWidth() + paddingLeft + paddingRight;
                            int newHeight = bitmap.getHeight() + paddingTop + paddingBottom;
                            Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, bitmap.getConfig());
                            Canvas canva = new Canvas(paddedBitmap);
                            canva.drawBitmap(bitmap, paddingLeft, paddingTop, null);
                            Objects.requireNonNull(switchSectionId.get(loadFlowModel.getOutput().getUndervoltage().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(loadFlowModel.getOutput().getUndervoltageColor())), 0, 0)));
                            loadFlowUnderVoltageDeviceID.put(loadFlowModel.getOutput().getUndervoltage().get(i).getId(), switchSectionId.get(loadFlowModel.getOutput().getUndervoltage().get(i).getId()));
                        }

                        if (capacitorSectionId.get(loadFlowModel.getOutput().getUndervoltage().get(i).getId()) != null) {
                            Objects.requireNonNull(capacitorSectionId.get(loadFlowModel.getOutput().getUndervoltage().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(loadFlowModel.getOutput().getUndervoltageColor())), 90, 0)));
                            loadFlowUnderVoltageDeviceID.put(loadFlowModel.getOutput().getUndervoltage().get(i).getId(), capacitorSectionId.get(loadFlowModel.getOutput().getUndervoltage().get(i).getId()));
                        }

                        if (spotLoadSectionId.get(loadFlowModel.getOutput().getUndervoltage().get(i).getId()) != null) {
                            if (spLineSectionList.contains(loadFlowModel.getOutput().getUndervoltage().get(i).getId())) {
                                int paddingPx = 0;
                                Bitmap paddedBitmap = Bitmap.createBitmap(BitmapImg.Spotload().getWidth() + paddingPx * 2, // Add padding to both sides
                                        BitmapImg.Spotload().getHeight() + paddingPx * 2, Bitmap.Config.ARGB_8888);
                                Canvas canvas1 = new Canvas(paddedBitmap);
                                canvas1.drawBitmap(BitmapImg.Spotload(), paddingPx, 10, null);
                                Objects.requireNonNull(spotLoadSectionId.get(loadFlowModel.getOutput().getUndervoltage().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(loadFlowModel.getOutput().getUndervoltageColor())), 0, 0)));
                                loadFlowUnderVoltageDeviceID.put(loadFlowModel.getOutput().getUndervoltage().get(i).getId(), spotLoadSectionId.get(loadFlowModel.getOutput().getUndervoltage().get(i).getId()));
                            } else {
                                Objects.requireNonNull(spotLoadSectionId.get(loadFlowModel.getOutput().getUndervoltage().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Spotload(), Color.parseColor(loadFlowModel.getOutput().getUndervoltageColor())), 0, 35)));
                                loadFlowUnderVoltageDeviceID.put(loadFlowModel.getOutput().getUndervoltage().get(i).getId(), spotLoadSectionId.get(loadFlowModel.getOutput().getUndervoltage().get(i).getId()));
                            }
                        }

                        if (!secNodeSectionId.isEmpty()) {
                            if (secNodeSectionId.get(loadFlowModel.getOutput().getUndervoltage().get(i).getId()) != null) {
                                Objects.requireNonNull(secNodeSectionId.get(loadFlowModel.getOutput().getUndervoltage().get(i).getId())).getPaint().setColor(Color.parseColor(loadFlowModel.getOutput().getUndervoltageColor()));
                                loadFlowUnderVoltageSectionID.put(loadFlowModel.getOutput().getUndervoltage().get(i).getId(), secNodeSectionId.get(loadFlowModel.getOutput().getUndervoltage().get(i).getId()));
                            }
                        }

                    }
                } else {
                    Status = "No Data";
                }

                if (loadFlowModel.getOutput().getOverload() != null && loadFlowModel.getOutput().getOverload().size() > 0) {
                    Config.isLoadFlow = true;
                    Config.isShortCircuit = false;
                    Config.isLoadAllocation = false;
                    Status = "Load Flow Complete!";
                    for (int i = 0; i < loadFlowModel.getOutput().getOverload().size(); i++) {
                        loadFlowOverLoadSectionId.add(loadFlowModel.getOutput().getOverload().get(i).getId());
                        if (CaSectionId.get(loadFlowModel.getOutput().getOverload().get(i).getId()) != null) {
                            Objects.requireNonNull(CaSectionId.get(loadFlowModel.getOutput().getOverload().get(i).getId())).getPaint().setColor(Color.parseColor(loadFlowModel.getOutput().getOverloadColor()));
                            loadFlowOverLoadSectionID.put(loadFlowModel.getOutput().getOverload().get(i).getId(), CaSectionId.get(loadFlowModel.getOutput().getOverload().get(i).getId()));
                        }

                        if (OhSectionId.get(loadFlowModel.getOutput().getOverload().get(i).getId()) != null) {
                            Objects.requireNonNull(OhSectionId.get(loadFlowModel.getOutput().getOverload().get(i).getId())).getPaint().setColor(Color.parseColor(loadFlowModel.getOutput().getOverloadColor()));
                            loadFlowOverLoadSectionID.put(loadFlowModel.getOutput().getOverload().get(i).getId(), OhSectionId.get(loadFlowModel.getOutput().getOverload().get(i).getId()));
                        }

                        if (UnBalSectionId.get(loadFlowModel.getOutput().getOverload().get(i).getId()) != null) {
                            Objects.requireNonNull(UnBalSectionId.get(loadFlowModel.getOutput().getOverload().get(i).getId())).getPaint().setColor(Color.parseColor(loadFlowModel.getOutput().getOverloadColor()));
                            loadFlowOverLoadSectionID.put(loadFlowModel.getOutput().getOverload().get(i).getId(), UnBalSectionId.get(loadFlowModel.getOutput().getOverload().get(i).getId()));
                        }

                        if (breakerSectionId.get(loadFlowModel.getOutput().getOverload().get(i).getId()) != null) {
                            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.breaker);
                            Bitmap bitmap = drawableToBitmap(drawable);
                            Objects.requireNonNull(breakerSectionId.get(loadFlowModel.getOutput().getOverload().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(loadFlowModel.getOutput().getOverloadColor())), 0, 0)));
                            loadFlowOverLoadDeviceID.put(loadFlowModel.getOutput().getOverload().get(i).getId(), breakerSectionId.get(loadFlowModel.getOutput().getOverload().get(i).getId()));
                        }

                        if (transformerSectionId.get(loadFlowModel.getOutput().getOverload().get(i).getId()) != null) {
                            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.transformer);
                            Bitmap bitmap = drawableToBitmap(drawable);
                            Objects.requireNonNull(transformerSectionId.get(loadFlowModel.getOutput().getOverload().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(loadFlowModel.getOutput().getOverloadColor())), 93, 0)));
                            loadFlowOverLoadDeviceID.put(loadFlowModel.getOutput().getOverload().get(i).getId(), transformerSectionId.get(loadFlowModel.getOutput().getOverload().get(i).getId()));
                        }

                        if (fuseSectionId.get(loadFlowModel.getOutput().getOverload().get(i).getId()) != null) {
                            Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "L", 90f, Color.BLACK);
                            Objects.requireNonNull(fuseSectionId.get(loadFlowModel.getOutput().getOverload().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(loadFlowModel.getOutput().getOverloadColor())), 0, 0)));
                            loadFlowOverLoadDeviceID.put(loadFlowModel.getOutput().getOverload().get(i).getId(), fuseSectionId.get(loadFlowModel.getOutput().getOverload().get(i).getId()));
                        }

                        if (switchSectionId.get(loadFlowModel.getOutput().getOverload().get(i).getId()) != null) {
                            Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "D", 70f, Color.BLACK);
                            int paddingLeft = 0; // Left padding in pixels
                            int paddingTop = 0; // Top padding in pixels
                            int paddingRight = 0; // Right padding in pixels
                            int paddingBottom = 12; // Bottom padding in pixels
                            int newWidth = bitmap.getWidth() + paddingLeft + paddingRight;
                            int newHeight = bitmap.getHeight() + paddingTop + paddingBottom;
                            Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, bitmap.getConfig());
                            Canvas canva = new Canvas(paddedBitmap);
                            canva.drawBitmap(bitmap, paddingLeft, paddingTop, null);
                            Objects.requireNonNull(switchSectionId.get(loadFlowModel.getOutput().getOverload().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(loadFlowModel.getOutput().getOverloadColor())), 0, 0)));
                            loadFlowOverLoadDeviceID.put(loadFlowModel.getOutput().getOverload().get(i).getId(), switchSectionId.get(loadFlowModel.getOutput().getOverload().get(i).getId()));
                        }

                        if (capacitorSectionId.get(loadFlowModel.getOutput().getOverload().get(i).getId()) != null) {
                            Objects.requireNonNull(capacitorSectionId.get(loadFlowModel.getOutput().getOverload().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(loadFlowModel.getOutput().getOverloadColor())), 90, 0)));
                            loadFlowOverLoadDeviceID.put(loadFlowModel.getOutput().getOverload().get(i).getId(), capacitorSectionId.get(loadFlowModel.getOutput().getOverload().get(i).getId()));
                        }

                        if (spotLoadSectionId.get(loadFlowModel.getOutput().getOverload().get(i).getId()) != null) {
                            if (spLineSectionList.contains(loadFlowModel.getOutput().getOverload().get(i).getId())) {
                                int paddingPx = 0;
                                Bitmap paddedBitmap = Bitmap.createBitmap(BitmapImg.Spotload().getWidth() + paddingPx * 2, // Add padding to both sides
                                        BitmapImg.Spotload().getHeight() + paddingPx * 2, Bitmap.Config.ARGB_8888);
                                Canvas canvas1 = new Canvas(paddedBitmap);
                                canvas1.drawBitmap(BitmapImg.Spotload(), paddingPx, 10, null);
                                Objects.requireNonNull(spotLoadSectionId.get(loadFlowModel.getOutput().getOverload().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(loadFlowModel.getOutput().getOverloadColor())), 0, 0)));
                                loadFlowOverLoadDeviceID.put(loadFlowModel.getOutput().getOverload().get(i).getId(), spotLoadSectionId.get(loadFlowModel.getOutput().getOverload().get(i).getId()));
                            } else {
                                Objects.requireNonNull(spotLoadSectionId.get(loadFlowModel.getOutput().getOverload().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Spotload(), Color.parseColor(loadFlowModel.getOutput().getOverloadColor())), 0, 35)));
                                loadFlowOverLoadDeviceID.put(loadFlowModel.getOutput().getOverload().get(i).getId(), spotLoadSectionId.get(loadFlowModel.getOutput().getOverload().get(i).getId()));
                            }
                        }

                        if (!secNodeSectionId.isEmpty()) {
                            if (secNodeSectionId.get(loadFlowModel.getOutput().getOverload().get(i).getId()) != null) {
                                Objects.requireNonNull(secNodeSectionId.get(loadFlowModel.getOutput().getOverload().get(i).getId())).getPaint().setColor(Color.parseColor(loadFlowModel.getOutput().getOverloadColor()));
                                loadFlowOverLoadSectionID.put(loadFlowModel.getOutput().getOverload().get(i).getId(), secNodeSectionId.get(loadFlowModel.getOutput().getOverload().get(i).getId()));
                            }
                        }
                    }
                } else {
                    Status = "No Data";
                }

            } catch (Exception e) {
                Config.isLoadFlow = false;
                Status = "Load Flow Failed!";
                Log.d("Exception", e.getLocalizedMessage());
            }
            return Status;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBarLayout.setVisibility(View.GONE);
            if (s.equals("Load Flow Complete!")) {
                Config.isLoadFlow = true;
                Config.isShortCircuit = false;
                Config.isLoadAllocation = false;
                binding.reportBtn.setVisibility(View.VISIBLE);
                Snackbar snack = Snackbar.make(findViewById(android.R.id.content), s, Snackbar.LENGTH_SHORT);
                snack.show();
            } else if (s.equals("No Data")) {
                Config.isLoadFlow = true;
                Config.isShortCircuit = false;
                Config.isLoadAllocation = false;
                binding.reportBtn.setVisibility(View.VISIBLE);
                Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Load Flow Run!", Snackbar.LENGTH_SHORT);
                snack.show();
            } else {
                Config.isLoadFlow = false;
                Config.isShortCircuit = false;
                Config.isLoadAllocation = false;
                binding.reportBtn.setVisibility(View.GONE);
                Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Load Flow Failed! Please Try Again Later", Snackbar.LENGTH_LONG);
                snack.show();
            }
            binding.map.invalidate();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class ShortCircuitAnalysis extends AsyncTask<Void, Void, String> {

        private final ShortCircuitModel shortCircuitModels;

        public ShortCircuitAnalysis(ShortCircuitModel shortCircuitModels) {
            this.shortCircuitModels = shortCircuitModels;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBarLayout.setVisibility(View.VISIBLE);
        }

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(Void... voids) {
            String Status;
            try {
                Status = "";

                if (shortCircuitModels.getOutput().getShortCircuitRatingColor() != null) {
                    ratingColors = shortCircuitModels.getOutput().getShortCircuitRatingColor();
                }

                if (shortCircuitModels.getOutput().getOverVoltageColor() != null) {
                    overVoltageColors = shortCircuitModels.getOutput().getOverVoltageColor();
                }

                if (shortCircuitModels.getOutput().getOverloadColor() != null) {
                    overloadColors = shortCircuitModels.getOutput().getOverloadColor();
                }

                if (shortCircuitModels.getOutput().getUndervoltageColor() != null) {
                    underVoltageColors = shortCircuitModels.getOutput().getUndervoltageColor();
                }

                if (shortCircuitModels.getOutput().getShortCircuitRating() != null && !shortCircuitModels.getOutput().getShortCircuitRating().isEmpty() && shortCircuitModels.getOutput().getShortCircuitRating().size() > 0) {
                    Config.isShortCircuit = true;
                    Config.isLoadFlow = false;
                    Config.isLoadAllocation = false;
                    Status = "Short Circuit Analysis Complete";
                    for (int i = 0; i < shortCircuitModels.getOutput().getShortCircuitRating().size(); i++) {
                        shortCircuitRatingSectionId.add(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId());
                        if (CaSectionId.get(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId()) != null) {
                            Objects.requireNonNull(CaSectionId.get(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId())).getPaint().setColor(Color.parseColor(shortCircuitModels.getOutput().getShortCircuitRatingColor()));
                            shortCircuitRatingSectionID.put(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId(), CaSectionId.get(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId()));
                        }

                        if (OhSectionId.get(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId()) != null) {
                            Objects.requireNonNull(OhSectionId.get(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId())).getPaint().setColor(Color.parseColor(shortCircuitModels.getOutput().getShortCircuitRatingColor()));
                            shortCircuitRatingSectionID.put(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId(), OhSectionId.get(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId()));
                        }

                        if (UnBalSectionId.get(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId()) != null) {
                            Objects.requireNonNull(UnBalSectionId.get(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId())).getPaint().setColor(Color.parseColor(shortCircuitModels.getOutput().getShortCircuitRatingColor()));
                            shortCircuitRatingSectionID.put(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId(), UnBalSectionId.get(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId()));
                        }

                        if (breakerSectionId.get(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId()) != null) {
                            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.breaker);
                            Bitmap bitmap = drawableToBitmap(drawable);
                            Objects.requireNonNull(breakerSectionId.get(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(shortCircuitModels.getOutput().getShortCircuitRatingColor())), 0, 0)));
                            shortCircuitRatingDeviceID.put(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId(), breakerSectionId.get(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId()));
                        }

                        if (transformerSectionId.get(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId()) != null) {
                            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.transformer);
                            Bitmap bitmap = drawableToBitmap(drawable);
                            Objects.requireNonNull(transformerSectionId.get(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(shortCircuitModels.getOutput().getShortCircuitRatingColor())), 93, 0)));
                            shortCircuitRatingDeviceID.put(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId(), transformerSectionId.get(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId()));
                        }

                        if (fuseSectionId.get(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId()) != null) {
                            Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "L", 90f, Color.BLACK);
                            Objects.requireNonNull(fuseSectionId.get(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(shortCircuitModels.getOutput().getShortCircuitRatingColor())), 0, 0)));
                            shortCircuitRatingDeviceID.put(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId(), fuseSectionId.get(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId()));
                        }

                        if (switchSectionId.get(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId()) != null) {
                            Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "D", 70f, Color.BLACK);
                            int paddingLeft = 0; // Left padding in pixels
                            int paddingTop = 0; // Top padding in pixels
                            int paddingRight = 0; // Right padding in pixels
                            int paddingBottom = 12; // Bottom padding in pixels
                            int newWidth = bitmap.getWidth() + paddingLeft + paddingRight;
                            int newHeight = bitmap.getHeight() + paddingTop + paddingBottom;
                            Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, bitmap.getConfig());
                            Canvas canva = new Canvas(paddedBitmap);
                            canva.drawBitmap(bitmap, paddingLeft, paddingTop, null);
                            Objects.requireNonNull(switchSectionId.get(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(shortCircuitModels.getOutput().getShortCircuitRatingColor())), 0, 0)));
                            shortCircuitRatingDeviceID.put(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId(), switchSectionId.get(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId()));
                        }

                        if (capacitorSectionId.get(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId()) != null) {
                            Objects.requireNonNull(capacitorSectionId.get(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(shortCircuitModels.getOutput().getShortCircuitRatingColor())), 90, 0)));
                            shortCircuitRatingDeviceID.put(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId(), capacitorSectionId.get(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId()));
                        }

                        if (spotLoadSectionId.get(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId()) != null) {
                            if (spLineSectionList.contains(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId())) {
                                int paddingPx = 0;
                                Bitmap paddedBitmap = Bitmap.createBitmap(BitmapImg.Spotload().getWidth() + paddingPx * 2, // Add padding to both sides
                                        BitmapImg.Spotload().getHeight() + paddingPx * 2, Bitmap.Config.ARGB_8888);
                                Canvas canvas1 = new Canvas(paddedBitmap);
                                canvas1.drawBitmap(BitmapImg.Spotload(), paddingPx, 10, null);
                                Objects.requireNonNull(spotLoadSectionId.get(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(shortCircuitModels.getOutput().getShortCircuitRatingColor())), 0, 0)));
                                shortCircuitRatingDeviceID.put(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId(), spotLoadSectionId.get(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId()));
                            } else {
                                Objects.requireNonNull(spotLoadSectionId.get(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Spotload(), Color.parseColor(shortCircuitModels.getOutput().getShortCircuitRatingColor())), 0, 35)));
                                shortCircuitRatingDeviceID.put(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId(), spotLoadSectionId.get(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId()));
                            }
                        }

                        if (!secNodeSectionId.isEmpty()) {
                            if (secNodeSectionId.get(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId()) != null) {
                                Objects.requireNonNull(secNodeSectionId.get(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId())).getPaint().setColor(Color.parseColor(shortCircuitModels.getOutput().getShortCircuitRatingColor()));
                                shortCircuitRatingSectionID.put(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId(), secNodeSectionId.get(shortCircuitModels.getOutput().getShortCircuitRating().get(i).getId()));
                            }
                        }
                    }
                } else {
                    Status = "No Data";
                }

                if (shortCircuitModels.getOutput().getOverVoltage() != null && !shortCircuitModels.getOutput().getOverVoltage().isEmpty() && shortCircuitModels.getOutput().getOverVoltage().size() > 0) {
                    Config.isShortCircuit = true;
                    Config.isLoadFlow = false;
                    Config.isLoadAllocation = false;
                    Status = "Short Circuit Analysis Complete";
                    for (int i = 0; i < shortCircuitModels.getOutput().getOverVoltage().size(); i++) {
                        shortCircuitOverVoltageSectionId.add(shortCircuitModels.getOutput().getOverVoltage().get(i).getId());
                        if (CaSectionId.get(shortCircuitModels.getOutput().getOverVoltage().get(i).getId()) != null) {
                            Objects.requireNonNull(CaSectionId.get(shortCircuitModels.getOutput().getOverVoltage().get(i).getId())).getPaint().setColor(Color.parseColor(shortCircuitModels.getOutput().getOverVoltageColor()));
                            shortCircuitOverVoltageSectionID.put(shortCircuitModels.getOutput().getOverVoltage().get(i).getId(), CaSectionId.get(shortCircuitModels.getOutput().getOverVoltage().get(i).getId()));
                        }

                        if (OhSectionId.get(shortCircuitModels.getOutput().getOverVoltage().get(i).getId()) != null) {
                            Objects.requireNonNull(OhSectionId.get(shortCircuitModels.getOutput().getOverVoltage().get(i).getId())).getPaint().setColor(Color.parseColor(shortCircuitModels.getOutput().getOverVoltageColor()));
                            shortCircuitOverVoltageSectionID.put(shortCircuitModels.getOutput().getOverVoltage().get(i).getId(), OhSectionId.get(shortCircuitModels.getOutput().getOverVoltage().get(i).getId()));
                        }

                        if (UnBalSectionId.get(shortCircuitModels.getOutput().getOverVoltage().get(i).getId()) != null) {
                            Objects.requireNonNull(UnBalSectionId.get(shortCircuitModels.getOutput().getOverVoltage().get(i).getId())).getPaint().setColor(Color.parseColor(shortCircuitModels.getOutput().getOverVoltageColor()));
                            shortCircuitOverVoltageSectionID.put(shortCircuitModels.getOutput().getOverVoltage().get(i).getId(), UnBalSectionId.get(shortCircuitModels.getOutput().getOverVoltage().get(i).getId()));
                        }

                        if (breakerSectionId.get(shortCircuitModels.getOutput().getOverVoltage().get(i).getId()) != null) {
                            @SuppressLint("UseCompatLoadingForDrawables")
                            Drawable drawable = getResources().getDrawable(R.drawable.breaker);
                            Bitmap bitmap = drawableToBitmap(drawable);
                            Objects.requireNonNull(breakerSectionId.get(shortCircuitModels.getOutput().getOverVoltage().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(shortCircuitModels.getOutput().getOverVoltageColor())), 0, 0)));
                            shortCircuitOverVoltageDeviceID.put(shortCircuitModels.getOutput().getOverVoltage().get(i).getId(), breakerSectionId.get(shortCircuitModels.getOutput().getOverVoltage().get(i).getId()));
                        }

                        if (transformerSectionId.get(shortCircuitModels.getOutput().getOverVoltage().get(i).getId()) != null) {
                            @SuppressLint("UseCompatLoadingForDrawables")
                            Drawable drawable = getResources().getDrawable(R.drawable.transformer);
                            Bitmap bitmap = drawableToBitmap(drawable);
                            Objects.requireNonNull(transformerSectionId.get(shortCircuitModels.getOutput().getOverVoltage().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(shortCircuitModels.getOutput().getOverVoltageColor())), 93, 0)));
                            shortCircuitOverVoltageDeviceID.put(shortCircuitModels.getOutput().getOverVoltage().get(i).getId(), transformerSectionId.get(shortCircuitModels.getOutput().getOverVoltage().get(i).getId()));
                        }

                        if (fuseSectionId.get(shortCircuitModels.getOutput().getOverVoltage().get(i).getId()) != null) {
                            Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "L", 90f, Color.BLACK);
                            Objects.requireNonNull(fuseSectionId.get(shortCircuitModels.getOutput().getOverVoltage().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(shortCircuitModels.getOutput().getOverVoltageColor())), 0, 0)));
                            shortCircuitOverVoltageDeviceID.put(shortCircuitModels.getOutput().getOverVoltage().get(i).getId(), fuseSectionId.get(shortCircuitModels.getOutput().getOverVoltage().get(i).getId()));
                        }

                        if (switchSectionId.get(shortCircuitModels.getOutput().getOverVoltage().get(i).getId()) != null) {
                            Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "D", 70f, Color.BLACK);
                            int paddingLeft = 0; // Left padding in pixels
                            int paddingTop = 0; // Top padding in pixels
                            int paddingRight = 0; // Right padding in pixels
                            int paddingBottom = 12; // Bottom padding in pixels
                            int newWidth = bitmap.getWidth() + paddingLeft + paddingRight;
                            int newHeight = bitmap.getHeight() + paddingTop + paddingBottom;
                            Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, bitmap.getConfig());
                            Canvas canva = new Canvas(paddedBitmap);
                            canva.drawBitmap(bitmap, paddingLeft, paddingTop, null);
                            Objects.requireNonNull(switchSectionId.get(shortCircuitModels.getOutput().getOverVoltage().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(shortCircuitModels.getOutput().getOverVoltageColor())), 0, 0)));
                            shortCircuitOverVoltageDeviceID.put(shortCircuitModels.getOutput().getOverVoltage().get(i).getId(), switchSectionId.get(shortCircuitModels.getOutput().getOverVoltage().get(i).getId()));
                        }

                        if (capacitorSectionId.get(shortCircuitModels.getOutput().getOverVoltage().get(i).getId()) != null) {
                            Objects.requireNonNull(capacitorSectionId.get(shortCircuitModels.getOutput().getOverVoltage().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(shortCircuitModels.getOutput().getOverVoltageColor())), 90, 0)));
                            shortCircuitOverVoltageDeviceID.put(shortCircuitModels.getOutput().getOverVoltage().get(i).getId(), capacitorSectionId.get(shortCircuitModels.getOutput().getOverVoltage().get(i).getId()));
                        }

                        if (spotLoadSectionId.get(shortCircuitModels.getOutput().getOverVoltage().get(i).getId()) != null) {
                            if (spLineSectionList.contains(shortCircuitModels.getOutput().getOverVoltage().get(i).getId())) {
                                int paddingPx = 0;
                                Bitmap paddedBitmap = Bitmap.createBitmap(BitmapImg.Spotload().getWidth() + paddingPx * 2, // Add padding to both sides
                                        BitmapImg.Spotload().getHeight() + paddingPx * 2, Bitmap.Config.ARGB_8888);
                                Canvas canvas1 = new Canvas(paddedBitmap);
                                canvas1.drawBitmap(BitmapImg.Spotload(), paddingPx, 10, null);
                                Objects.requireNonNull(spotLoadSectionId.get(shortCircuitModels.getOutput().getOverVoltage().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(shortCircuitModels.getOutput().getOverVoltageColor())), 0, 0)));
                                shortCircuitOverVoltageDeviceID.put(shortCircuitModels.getOutput().getOverVoltage().get(i).getId(), spotLoadSectionId.get(shortCircuitModels.getOutput().getOverVoltage().get(i).getId()));
                            } else {
                                Objects.requireNonNull(spotLoadSectionId.get(shortCircuitModels.getOutput().getOverVoltage().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Spotload(), Color.parseColor(shortCircuitModels.getOutput().getOverVoltageColor())), 0, 35)));
                                shortCircuitOverVoltageDeviceID.put(shortCircuitModels.getOutput().getOverVoltage().get(i).getId(), spotLoadSectionId.get(shortCircuitModels.getOutput().getOverVoltage().get(i).getId()));
                            }
                        }

                        if (!secNodeSectionId.isEmpty()) {
                            if (secNodeSectionId.get(shortCircuitModels.getOutput().getOverVoltage().get(i).getId()) != null) {
                                Objects.requireNonNull(secNodeSectionId.get(shortCircuitModels.getOutput().getOverVoltage().get(i).getId())).getPaint().setColor(Color.parseColor(shortCircuitModels.getOutput().getOverVoltageColor()));
                                shortCircuitOverVoltageSectionID.put(shortCircuitModels.getOutput().getOverVoltage().get(i).getId(), secNodeSectionId.get(shortCircuitModels.getOutput().getOverVoltage().get(i).getId()));
                            }
                        }

                    }
                } else {
                    Status = "No Data";
                }

                if (shortCircuitModels.getOutput().getOverload() != null && !shortCircuitModels.getOutput().getOverload().isEmpty() && shortCircuitModels.getOutput().getOverload().size() > 0) {
                    Config.isShortCircuit = true;
                    Config.isLoadFlow = false;
                    Config.isLoadAllocation = false;
                    Status = "Short Circuit Analysis Complete";
                    for (int i = 0; i < shortCircuitModels.getOutput().getOverload().size(); i++) {
                        shortCircuitOverLoadSectionId.add(shortCircuitModels.getOutput().getOverload().get(i).getId());
                        if (CaSectionId.get(shortCircuitModels.getOutput().getOverload().get(i).getId()) != null) {
                            Objects.requireNonNull(CaSectionId.get(shortCircuitModels.getOutput().getOverload().get(i).getId())).getPaint().setColor(Color.parseColor(shortCircuitModels.getOutput().getOverloadColor()));
                            shortCircuitOverLoadSectionID.put(shortCircuitModels.getOutput().getOverload().get(i).getId(), CaSectionId.get(shortCircuitModels.getOutput().getOverload().get(i).getId()));
                        }

                        if (OhSectionId.get(shortCircuitModels.getOutput().getOverload().get(i).getId()) != null) {
                            Objects.requireNonNull(OhSectionId.get(shortCircuitModels.getOutput().getOverload().get(i).getId())).getPaint().setColor(Color.parseColor(shortCircuitModels.getOutput().getOverloadColor()));
                            shortCircuitOverLoadSectionID.put(shortCircuitModels.getOutput().getOverload().get(i).getId(), OhSectionId.get(shortCircuitModels.getOutput().getOverload().get(i).getId()));
                        }

                        if (UnBalSectionId.get(shortCircuitModels.getOutput().getOverload().get(i).getId()) != null) {
                            Objects.requireNonNull(UnBalSectionId.get(shortCircuitModels.getOutput().getOverload().get(i).getId())).getPaint().setColor(Color.parseColor(shortCircuitModels.getOutput().getOverloadColor()));
                            shortCircuitOverLoadSectionID.put(shortCircuitModels.getOutput().getOverload().get(i).getId(), UnBalSectionId.get(shortCircuitModels.getOutput().getOverload().get(i).getId()));
                        }

                        if (breakerSectionId.get(shortCircuitModels.getOutput().getOverload().get(i).getId()) != null) {
                            @SuppressLint("UseCompatLoadingForDrawables")
                            Drawable drawable = getResources().getDrawable(R.drawable.breaker);
                            Bitmap bitmap = drawableToBitmap(drawable);
                            Objects.requireNonNull(breakerSectionId.get(shortCircuitModels.getOutput().getOverload().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(shortCircuitModels.getOutput().getOverloadColor())), 0, 0)));
                            shortCircuitOverLoadDeviceID.put(shortCircuitModels.getOutput().getOverload().get(i).getId(), breakerSectionId.get(shortCircuitModels.getOutput().getOverload().get(i).getId()));
                        }

                        if (transformerSectionId.get(shortCircuitModels.getOutput().getOverload().get(i).getId()) != null) {
                            @SuppressLint("UseCompatLoadingForDrawables")
                            Drawable drawable = getResources().getDrawable(R.drawable.transformer);
                            Bitmap bitmap = drawableToBitmap(drawable);
                            Objects.requireNonNull(transformerSectionId.get(shortCircuitModels.getOutput().getOverload().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(shortCircuitModels.getOutput().getOverloadColor())), 93, 0)));
                            shortCircuitOverLoadDeviceID.put(shortCircuitModels.getOutput().getOverload().get(i).getId(), breakerSectionId.get(shortCircuitModels.getOutput().getOverload().get(i).getId()));
                        }

                        if (fuseSectionId.get(shortCircuitModels.getOutput().getOverload().get(i).getId()) != null) {
                            Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "L", 90f, Color.BLACK);
                            Objects.requireNonNull(fuseSectionId.get(shortCircuitModels.getOutput().getOverload().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(shortCircuitModels.getOutput().getOverloadColor())), 0, 0)));
                            shortCircuitOverLoadDeviceID.put(shortCircuitModels.getOutput().getOverload().get(i).getId(), fuseSectionId.get(shortCircuitModels.getOutput().getOverload().get(i).getId()));
                        }

                        if (switchSectionId.get(shortCircuitModels.getOutput().getOverload().get(i).getId()) != null) {
                            Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "D", 70f, Color.BLACK);
                            int paddingLeft = 0; // Left padding in pixels
                            int paddingTop = 0; // Top padding in pixels
                            int paddingRight = 0; // Right padding in pixels
                            int paddingBottom = 12; // Bottom padding in pixels
                            int newWidth = bitmap.getWidth() + paddingLeft + paddingRight;
                            int newHeight = bitmap.getHeight() + paddingTop + paddingBottom;
                            Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, bitmap.getConfig());
                            Canvas canva = new Canvas(paddedBitmap);
                            canva.drawBitmap(bitmap, paddingLeft, paddingTop, null);
                            Objects.requireNonNull(switchSectionId.get(shortCircuitModels.getOutput().getOverload().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(shortCircuitModels.getOutput().getOverloadColor())), 0, 0)));
                            shortCircuitOverLoadDeviceID.put(shortCircuitModels.getOutput().getOverload().get(i).getId(), switchSectionId.get(shortCircuitModels.getOutput().getOverload().get(i).getId()));
                        }

                        if (capacitorSectionId.get(shortCircuitModels.getOutput().getOverload().get(i).getId()) != null) {
                            Objects.requireNonNull(capacitorSectionId.get(shortCircuitModels.getOutput().getOverload().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(shortCircuitModels.getOutput().getOverloadColor())), 90, 0)));
                            shortCircuitOverLoadDeviceID.put(shortCircuitModels.getOutput().getOverload().get(i).getId(), capacitorSectionId.get(shortCircuitModels.getOutput().getOverload().get(i).getId()));
                        }

                        if (spotLoadSectionId.get(shortCircuitModels.getOutput().getOverload().get(i).getId()) != null) {
                            if (spLineSectionList.contains(shortCircuitModels.getOutput().getOverload().get(i).getId())) {
                                int paddingPx = 0;
                                Bitmap paddedBitmap = Bitmap.createBitmap(BitmapImg.Spotload().getWidth() + paddingPx * 2, // Add padding to both sides
                                        BitmapImg.Spotload().getHeight() + paddingPx * 2, Bitmap.Config.ARGB_8888);
                                Canvas canvas1 = new Canvas(paddedBitmap);
                                canvas1.drawBitmap(BitmapImg.Spotload(), paddingPx, 10, null);
                                Objects.requireNonNull(spotLoadSectionId.get(shortCircuitModels.getOutput().getOverload().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(shortCircuitModels.getOutput().getOverloadColor())), 0, 0)));
                                shortCircuitOverLoadDeviceID.put(shortCircuitModels.getOutput().getOverload().get(i).getId(), spotLoadSectionId.get(shortCircuitModels.getOutput().getOverload().get(i).getId()));
                            } else {
                                Objects.requireNonNull(spotLoadSectionId.get(shortCircuitModels.getOutput().getOverload().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Spotload(), Color.parseColor(shortCircuitModels.getOutput().getOverloadColor())), 0, 35)));
                                shortCircuitOverLoadDeviceID.put(shortCircuitModels.getOutput().getOverload().get(i).getId(), spotLoadSectionId.get(shortCircuitModels.getOutput().getOverload().get(i).getId()));
                            }
                        }

                        if (!secNodeSectionId.isEmpty()) {
                            if (secNodeSectionId.get(shortCircuitModels.getOutput().getOverload().get(i).getId()) != null) {
                                Objects.requireNonNull(secNodeSectionId.get(shortCircuitModels.getOutput().getOverload().get(i).getId())).getPaint().setColor(Color.parseColor(shortCircuitModels.getOutput().getOverloadColor()));
                                shortCircuitOverLoadSectionID.put(shortCircuitModels.getOutput().getOverload().get(i).getId(), secNodeSectionId.get(shortCircuitModels.getOutput().getOverload().get(i).getId()));
                            }
                        }

                    }
                } else {
                    Status = "No Data";
                }

                if (shortCircuitModels.getOutput().getUndervoltage() != null && !shortCircuitModels.getOutput().getUndervoltage().isEmpty() && shortCircuitModels.getOutput().getUndervoltage().size() > 0) {
                    Config.isShortCircuit = true;
                    Config.isLoadFlow = false;
                    Config.isLoadAllocation = false;
                    Status = "Short Circuit Analysis Complete";
                    for (int i = 0; i < shortCircuitModels.getOutput().getUndervoltage().size(); i++) {
                        shortCircuitUnderVoltageSectionId.add(shortCircuitModels.getOutput().getUndervoltage().get(i).getId());
                        if (CaSectionId.get(shortCircuitModels.getOutput().getUndervoltage().get(i).getId()) != null) {
                            Objects.requireNonNull(CaSectionId.get(shortCircuitModels.getOutput().getUndervoltage().get(i).getId())).getPaint().setColor(Color.parseColor(shortCircuitModels.getOutput().getUndervoltageColor()));
                            shortCircuitUnderVoltageSectionID.put(shortCircuitModels.getOutput().getUndervoltage().get(i).getId(), CaSectionId.get(shortCircuitModels.getOutput().getUndervoltage().get(i).getId()));
                        }

                        if (OhSectionId.get(shortCircuitModels.getOutput().getUndervoltage().get(i).getId()) != null) {
                            Objects.requireNonNull(OhSectionId.get(shortCircuitModels.getOutput().getUndervoltage().get(i).getId())).getPaint().setColor(Color.parseColor(shortCircuitModels.getOutput().getUndervoltageColor()));
                            shortCircuitUnderVoltageSectionID.put(shortCircuitModels.getOutput().getUndervoltage().get(i).getId(), OhSectionId.get(shortCircuitModels.getOutput().getUndervoltage().get(i).getId()));
                        }

                        if (UnBalSectionId.get(shortCircuitModels.getOutput().getUndervoltage().get(i).getId()) != null) {
                            Objects.requireNonNull(UnBalSectionId.get(shortCircuitModels.getOutput().getUndervoltage().get(i).getId())).getPaint().setColor(Color.parseColor(shortCircuitModels.getOutput().getUndervoltageColor()));
                            shortCircuitUnderVoltageSectionID.put(shortCircuitModels.getOutput().getUndervoltage().get(i).getId(), UnBalSectionId.get(shortCircuitModels.getOutput().getUndervoltage().get(i).getId()));
                        }

                        if (breakerSectionId.get(shortCircuitModels.getOutput().getUndervoltage().get(i).getId()) != null) {
                            @SuppressLint("UseCompatLoadingForDrawables")
                            Drawable drawable = getResources().getDrawable(R.drawable.breaker);
                            Bitmap bitmap = drawableToBitmap(drawable);
                            Objects.requireNonNull(breakerSectionId.get(shortCircuitModels.getOutput().getUndervoltage().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(shortCircuitModels.getOutput().getUndervoltageColor())), 0, 0)));
                            shortCircuitUnderVoltageDeviceID.put(shortCircuitModels.getOutput().getUndervoltage().get(i).getId(), breakerSectionId.get(shortCircuitModels.getOutput().getUndervoltage().get(i).getId()));
                        }

                        if (transformerSectionId.get(shortCircuitModels.getOutput().getUndervoltage().get(i).getId()) != null) {
                            @SuppressLint("UseCompatLoadingForDrawables")
                            Drawable drawable = getResources().getDrawable(R.drawable.transformer);
                            Bitmap bitmap = drawableToBitmap(drawable);
                            Objects.requireNonNull(transformerSectionId.get(shortCircuitModels.getOutput().getUndervoltage().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(shortCircuitModels.getOutput().getUndervoltageColor())), 93, 0)));
                            shortCircuitUnderVoltageDeviceID.put(shortCircuitModels.getOutput().getUndervoltage().get(i).getId(), transformerSectionId.get(shortCircuitModels.getOutput().getUndervoltage().get(i).getId()));
                        }

                        if (fuseSectionId.get(shortCircuitModels.getOutput().getUndervoltage().get(i).getId()) != null) {
                            Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "L", 90f, Color.BLACK);
                            Objects.requireNonNull(fuseSectionId.get(shortCircuitModels.getOutput().getUndervoltage().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(shortCircuitModels.getOutput().getUndervoltageColor())), 0, 0)));
                            shortCircuitUnderVoltageDeviceID.put(shortCircuitModels.getOutput().getUndervoltage().get(i).getId(), fuseSectionId.get(shortCircuitModels.getOutput().getUndervoltage().get(i).getId()));
                        }

                        if (switchSectionId.get(shortCircuitModels.getOutput().getUndervoltage().get(i).getId()) != null) {
                            Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "D", 70f, Color.BLACK);
                            int paddingLeft = 0; // Left padding in pixels
                            int paddingTop = 0; // Top padding in pixels
                            int paddingRight = 0; // Right padding in pixels
                            int paddingBottom = 12; // Bottom padding in pixels
                            int newWidth = bitmap.getWidth() + paddingLeft + paddingRight;
                            int newHeight = bitmap.getHeight() + paddingTop + paddingBottom;
                            Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, bitmap.getConfig());
                            Canvas canva = new Canvas(paddedBitmap);
                            canva.drawBitmap(bitmap, paddingLeft, paddingTop, null);
                            Objects.requireNonNull(switchSectionId.get(shortCircuitModels.getOutput().getUndervoltage().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(shortCircuitModels.getOutput().getUndervoltageColor())), 0, 0)));
                            shortCircuitUnderVoltageDeviceID.put(shortCircuitModels.getOutput().getUndervoltage().get(i).getId(), switchSectionId.get(shortCircuitModels.getOutput().getUndervoltage().get(i).getId()));
                        }

                        if (capacitorSectionId.get(shortCircuitModels.getOutput().getUndervoltage().get(i).getId()) != null) {
                            Objects.requireNonNull(capacitorSectionId.get(shortCircuitModels.getOutput().getUndervoltage().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(shortCircuitModels.getOutput().getUndervoltageColor())), 90, 0)));
                            shortCircuitUnderVoltageDeviceID.put(shortCircuitModels.getOutput().getUndervoltage().get(i).getId(), capacitorSectionId.get(shortCircuitModels.getOutput().getUndervoltage().get(i).getId()));
                        }

                        if (spotLoadSectionId.get(shortCircuitModels.getOutput().getUndervoltage().get(i).getId()) != null) {
                            if (spLineSectionList.contains(shortCircuitModels.getOutput().getUndervoltage().get(i).getId())) {
                                int paddingPx = 0;
                                Bitmap paddedBitmap = Bitmap.createBitmap(BitmapImg.Spotload().getWidth() + paddingPx * 2, // Add padding to both sides
                                        BitmapImg.Spotload().getHeight() + paddingPx * 2, Bitmap.Config.ARGB_8888);
                                Canvas canvas1 = new Canvas(paddedBitmap);
                                canvas1.drawBitmap(BitmapImg.Spotload(), paddingPx, 10, null);
                                Objects.requireNonNull(spotLoadSectionId.get(shortCircuitModels.getOutput().getUndervoltage().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(shortCircuitModels.getOutput().getUndervoltageColor())), 0, 0)));
                                shortCircuitUnderVoltageDeviceID.put(shortCircuitModels.getOutput().getUndervoltage().get(i).getId(), spotLoadSectionId.get(shortCircuitModels.getOutput().getUndervoltage().get(i).getId()));
                            } else {
                                Objects.requireNonNull(spotLoadSectionId.get(shortCircuitModels.getOutput().getUndervoltage().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Spotload(), Color.parseColor(shortCircuitModels.getOutput().getUndervoltageColor())), 0, 35)));
                                shortCircuitUnderVoltageDeviceID.put(shortCircuitModels.getOutput().getUndervoltage().get(i).getId(), spotLoadSectionId.get(shortCircuitModels.getOutput().getUndervoltage().get(i).getId()));
                            }
                        }

                        if (!secNodeSectionId.isEmpty()) {
                            if (secNodeSectionId.get(shortCircuitModels.getOutput().getUndervoltage().get(i).getId()) != null) {
                                Objects.requireNonNull(secNodeSectionId.get(shortCircuitModels.getOutput().getUndervoltage().get(i).getId())).getPaint().setColor(Color.parseColor(shortCircuitModels.getOutput().getUndervoltageColor()));
                                shortCircuitUnderVoltageSectionID.put(shortCircuitModels.getOutput().getUndervoltage().get(i).getId(), secNodeSectionId.get(shortCircuitModels.getOutput().getUndervoltage().get(i).getId()));
                            }
                        }
                    }
                } else {
                    Status = "No Data";
                }

            } catch (Exception e) {
                Config.isShortCircuit = false;
                Status = "Short Circuit Analysis Failed";
                Log.d("Exception", e.getLocalizedMessage());
            }
            return Status;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBarLayout.setVisibility(View.GONE);
            if (s.equals("Short Circuit Analysis Complete")) {
                Config.isShortCircuit = true;
                Config.isLoadFlow = false;
                Config.isLoadAllocation = false;
                binding.reportBtn.setVisibility(View.VISIBLE);
                Snackbar snack = Snackbar.make(findViewById(android.R.id.content), s, Snackbar.LENGTH_LONG);
                snack.show();
            } else if (s.equals("No Data")) {
                Config.isShortCircuit = true;
                Config.isLoadFlow = false;
                Config.isLoadAllocation = false;
                binding.reportBtn.setVisibility(View.VISIBLE);
                Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Short Circuit Analysis Run!", Snackbar.LENGTH_SHORT);
                snack.show();
            } else {
                Config.isShortCircuit = false;
                Config.isLoadFlow = false;
                Config.isLoadAllocation = false;
                binding.reportBtn.setVisibility(View.GONE);
                Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Load Flow Failed! Please Try Again Later", Snackbar.LENGTH_LONG);
                snack.show();
            }
            binding.map.invalidate();
        }

    }

    @SuppressLint("StaticFieldLeak")
    private class LoadAllocationAnalysis extends AsyncTask<Void, Void, String> {

        private final LoadAllocationModel loadAllocationModel;

        public LoadAllocationAnalysis(LoadAllocationModel loadAllocationModel) {
            this.loadAllocationModel = loadAllocationModel;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(Void... voids) {
            String Status;
            try {
                Status = "";

                if (loadAllocationModel.getOutput().getOverVoltageColor() != null) {
                    overVoltageColors = loadAllocationModel.getOutput().getOverVoltageColor();
                }

                if (loadAllocationModel.getOutput().getOverloadColor() != null) {
                    overloadColors = loadAllocationModel.getOutput().getOverloadColor();
                }

                if (loadAllocationModel.getOutput().getUndervoltageColor() != null) {
                    underVoltageColors = loadAllocationModel.getOutput().getUndervoltageColor();
                }

                if (loadAllocationModel.getOutput().getOverVoltage() != null && !loadAllocationModel.getOutput().getOverVoltage().isEmpty() && loadAllocationModel.getOutput().getOverVoltage().size() > 0) {
                    Config.isLoadAllocation = true;
                    Config.isLoadFlow = false;
                    Config.isShortCircuit = false;
                    Status = "Load Allocation Analysis Complete";
                    for (int i = 0; i < loadAllocationModel.getOutput().getOverVoltage().size(); i++) {

                        if (CaSectionId.get(loadAllocationModel.getOutput().getOverVoltage().get(i).getId()) != null) {
                            Objects.requireNonNull(CaSectionId.get(loadAllocationModel.getOutput().getOverVoltage().get(i).getId())).getPaint().setColor(Color.parseColor(loadAllocationModel.getOutput().getOverVoltageColor()));
                        }

                        if (OhSectionId.get(loadAllocationModel.getOutput().getOverVoltage().get(i).getId()) != null) {
                            Objects.requireNonNull(OhSectionId.get(loadAllocationModel.getOutput().getOverVoltage().get(i).getId())).getPaint().setColor(Color.parseColor(loadAllocationModel.getOutput().getOverVoltageColor()));
                        }

                        if (UnBalSectionId.get(loadAllocationModel.getOutput().getOverVoltage().get(i).getId()) != null) {
                            Objects.requireNonNull(UnBalSectionId.get(loadAllocationModel.getOutput().getOverVoltage().get(i).getId())).getPaint().setColor(Color.parseColor(loadAllocationModel.getOutput().getOverVoltageColor()));
                        }

                        if (breakerSectionId.get(loadAllocationModel.getOutput().getOverVoltage().get(i).getId()) != null) {
                            @SuppressLint("UseCompatLoadingForDrawables")
                            Drawable drawable = getResources().getDrawable(R.drawable.breaker);
                            Bitmap bitmap = drawableToBitmap(drawable);
                            Objects.requireNonNull(breakerSectionId.get(loadAllocationModel.getOutput().getOverVoltage().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(loadAllocationModel.getOutput().getOverVoltageColor())), 0, 0)));
                        }

                        if (transformerSectionId.get(loadAllocationModel.getOutput().getOverVoltage().get(i).getId()) != null) {
                            @SuppressLint("UseCompatLoadingForDrawables")
                            Drawable drawable = getResources().getDrawable(R.drawable.transformer);
                            Bitmap bitmap = drawableToBitmap(drawable);
                            Objects.requireNonNull(transformerSectionId.get(loadAllocationModel.getOutput().getOverVoltage().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(loadAllocationModel.getOutput().getOverVoltageColor())), 93, 0)));
                        }

                        if (fuseSectionId.get(loadAllocationModel.getOutput().getOverVoltage().get(i).getId()) != null) {
                            Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "L", 90f, Color.BLACK);
                            Objects.requireNonNull(fuseSectionId.get(loadAllocationModel.getOutput().getOverVoltage().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(loadAllocationModel.getOutput().getOverVoltageColor())), 0, 0)));
                        }

                        if (switchSectionId.get(loadAllocationModel.getOutput().getOverVoltage().get(i).getId()) != null) {
                            Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "D", 70f, Color.BLACK);
                            int paddingLeft = 0; // Left padding in pixels
                            int paddingTop = 0; // Top padding in pixels
                            int paddingRight = 0; // Right padding in pixels
                            int paddingBottom = 12; // Bottom padding in pixels
                            int newWidth = bitmap.getWidth() + paddingLeft + paddingRight;
                            int newHeight = bitmap.getHeight() + paddingTop + paddingBottom;
                            Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, bitmap.getConfig());
                            Canvas canva = new Canvas(paddedBitmap);
                            canva.drawBitmap(bitmap, paddingLeft, paddingTop, null);
                            Objects.requireNonNull(switchSectionId.get(loadAllocationModel.getOutput().getOverVoltage().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(loadAllocationModel.getOutput().getOverVoltageColor())), 0, 0)));
                        }

                        if (capacitorSectionId.get(loadAllocationModel.getOutput().getOverVoltage().get(i).getId()) != null) {
                            Objects.requireNonNull(capacitorSectionId.get(loadAllocationModel.getOutput().getOverVoltage().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(loadAllocationModel.getOutput().getOverVoltageColor())), 90, 0)));
                        }

                        if (spotLoadSectionId.get(loadAllocationModel.getOutput().getOverVoltage().get(i).getId()) != null) {
                            if (spLineSectionList.contains(loadAllocationModel.getOutput().getOverVoltage().get(i).getId())) {
                                int paddingPx = 0;
                                Bitmap paddedBitmap = Bitmap.createBitmap(BitmapImg.Spotload().getWidth() + paddingPx * 2, // Add padding to both sides
                                        BitmapImg.Spotload().getHeight() + paddingPx * 2, Bitmap.Config.ARGB_8888);
                                Canvas canvas1 = new Canvas(paddedBitmap);
                                canvas1.drawBitmap(BitmapImg.Spotload(), paddingPx, 10, null);
                                Objects.requireNonNull(spotLoadSectionId.get(loadAllocationModel.getOutput().getOverVoltage().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(loadAllocationModel.getOutput().getOverVoltageColor())), 0, 0)));
                            } else {
                                Objects.requireNonNull(spotLoadSectionId.get(loadAllocationModel.getOutput().getOverVoltage().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Spotload(), Color.parseColor(loadAllocationModel.getOutput().getOverVoltageColor())), 0, 35)));
                            }
                        }

                        if (!secNodeSectionId.isEmpty()) {
                            if (secNodeSectionId.get(loadAllocationModel.getOutput().getOverVoltage().get(i).getId()) != null) {
                                Objects.requireNonNull(secNodeSectionId.get(loadAllocationModel.getOutput().getOverVoltage().get(i).getId())).getPaint().setColor(Color.parseColor(loadAllocationModel.getOutput().getOverVoltageColor()));
                            }
                        }
                    }
                }

                if (loadAllocationModel.getOutput().getOverload() != null && !loadAllocationModel.getOutput().getOverload().isEmpty() && loadAllocationModel.getOutput().getOverload().size() > 0) {
                    Config.isLoadAllocation = true;
                    Config.isLoadFlow = false;
                    Config.isShortCircuit = false;
                    Status = "Load Allocation Analysis Complete";
                    for (int i = 0; i < loadAllocationModel.getOutput().getOverload().size(); i++) {

                        if (CaSectionId.get(loadAllocationModel.getOutput().getOverload().get(i).getId()) != null) {
                            Objects.requireNonNull(CaSectionId.get(loadAllocationModel.getOutput().getOverload().get(i).getId())).getPaint().setColor(Color.parseColor(loadAllocationModel.getOutput().getOverloadColor()));
                        }

                        if (OhSectionId.get(loadAllocationModel.getOutput().getOverload().get(i).getId()) != null) {
                            Objects.requireNonNull(OhSectionId.get(loadAllocationModel.getOutput().getOverload().get(i).getId())).getPaint().setColor(Color.parseColor(loadAllocationModel.getOutput().getOverloadColor()));
                        }

                        if (UnBalSectionId.get(loadAllocationModel.getOutput().getOverload().get(i).getId()) != null) {
                            Objects.requireNonNull(UnBalSectionId.get(loadAllocationModel.getOutput().getOverload().get(i).getId())).getPaint().setColor(Color.parseColor(loadAllocationModel.getOutput().getOverloadColor()));
                        }

                        if (breakerSectionId.get(loadAllocationModel.getOutput().getOverload().get(i).getId()) != null) {
                            @SuppressLint("UseCompatLoadingForDrawables")
                            Drawable drawable = getResources().getDrawable(R.drawable.breaker);
                            Bitmap bitmap = drawableToBitmap(drawable);
                            Objects.requireNonNull(breakerSectionId.get(loadAllocationModel.getOutput().getOverload().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(loadAllocationModel.getOutput().getOverloadColor())), 0, 0)));
                        }

                        if (transformerSectionId.get(loadAllocationModel.getOutput().getOverload().get(i).getId()) != null) {
                            @SuppressLint("UseCompatLoadingForDrawables")
                            Drawable drawable = getResources().getDrawable(R.drawable.transformer);
                            Bitmap bitmap = drawableToBitmap(drawable);
                            Objects.requireNonNull(transformerSectionId.get(loadAllocationModel.getOutput().getOverload().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(loadAllocationModel.getOutput().getOverloadColor())), 93, 0)));
                        }

                        if (fuseSectionId.get(loadAllocationModel.getOutput().getOverload().get(i).getId()) != null) {
                            Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "L", 90f, Color.BLACK);
                            Objects.requireNonNull(fuseSectionId.get(loadAllocationModel.getOutput().getOverload().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(loadAllocationModel.getOutput().getOverloadColor())), 0, 0)));
                        }

                        if (switchSectionId.get(loadAllocationModel.getOutput().getOverload().get(i).getId()) != null) {
                            Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "D", 70f, Color.BLACK);
                            int paddingLeft = 0; // Left padding in pixels
                            int paddingTop = 0; // Top padding in pixels
                            int paddingRight = 0; // Right padding in pixels
                            int paddingBottom = 12; // Bottom padding in pixels
                            int newWidth = bitmap.getWidth() + paddingLeft + paddingRight;
                            int newHeight = bitmap.getHeight() + paddingTop + paddingBottom;
                            Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, bitmap.getConfig());
                            Canvas canva = new Canvas(paddedBitmap);
                            canva.drawBitmap(bitmap, paddingLeft, paddingTop, null);
                            Objects.requireNonNull(switchSectionId.get(loadAllocationModel.getOutput().getOverload().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(loadAllocationModel.getOutput().getOverloadColor())), 0, 0)));
                        }

                        if (capacitorSectionId.get(loadAllocationModel.getOutput().getOverload().get(i).getId()) != null) {
                            Objects.requireNonNull(capacitorSectionId.get(loadAllocationModel.getOutput().getOverload().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(loadAllocationModel.getOutput().getOverloadColor())), 90, 0)));
                        }

                        if (spotLoadSectionId.get(loadAllocationModel.getOutput().getOverload().get(i).getId()) != null) {
                            if (spLineSectionList.contains(loadAllocationModel.getOutput().getOverload().get(i).getId())) {
                                int paddingPx = 0;
                                Bitmap paddedBitmap = Bitmap.createBitmap(BitmapImg.Spotload().getWidth() + paddingPx * 2, // Add padding to both sides
                                        BitmapImg.Spotload().getHeight() + paddingPx * 2, Bitmap.Config.ARGB_8888);
                                Canvas canvas1 = new Canvas(paddedBitmap);
                                canvas1.drawBitmap(BitmapImg.Spotload(), paddingPx, 10, null);
                                Objects.requireNonNull(spotLoadSectionId.get(loadAllocationModel.getOutput().getOverload().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(loadAllocationModel.getOutput().getOverloadColor())), 0, 0)));
                            } else {
                                Objects.requireNonNull(spotLoadSectionId.get(loadAllocationModel.getOutput().getOverload().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Spotload(), Color.parseColor(loadAllocationModel.getOutput().getOverloadColor())), 0, 35)));
                            }
                        }

                        if (!secNodeSectionId.isEmpty()) {
                            if (secNodeSectionId.get(loadAllocationModel.getOutput().getOverload().get(i).getId()) != null) {
                                Objects.requireNonNull(secNodeSectionId.get(loadAllocationModel.getOutput().getOverload().get(i).getId())).getPaint().setColor(Color.parseColor(loadAllocationModel.getOutput().getOverloadColor()));
                            }
                        }
                    }
                }

                if (loadAllocationModel.getOutput().getUndervoltage() != null && !loadAllocationModel.getOutput().getUndervoltage().isEmpty() && loadAllocationModel.getOutput().getUndervoltage().size() > 0) {
                    Config.isLoadAllocation = true;
                    Config.isLoadFlow = false;
                    Config.isShortCircuit = false;
                    Status = "Load Allocation Analysis Complete";
                    for (int i = 0; i < loadAllocationModel.getOutput().getUndervoltage().size(); i++) {

                        if (CaSectionId.get(loadAllocationModel.getOutput().getUndervoltage().get(i).getId()) != null) {
                            Objects.requireNonNull(CaSectionId.get(loadAllocationModel.getOutput().getUndervoltage().get(i).getId())).getPaint().setColor(Color.parseColor(loadAllocationModel.getOutput().getUndervoltageColor()));
                        }

                        if (OhSectionId.get(loadAllocationModel.getOutput().getUndervoltage().get(i).getId()) != null) {
                            Objects.requireNonNull(OhSectionId.get(loadAllocationModel.getOutput().getUndervoltage().get(i).getId())).getPaint().setColor(Color.parseColor(loadAllocationModel.getOutput().getUndervoltageColor()));
                        }

                        if (UnBalSectionId.get(loadAllocationModel.getOutput().getUndervoltage().get(i).getId()) != null) {
                            Objects.requireNonNull(UnBalSectionId.get(loadAllocationModel.getOutput().getUndervoltage().get(i).getId())).getPaint().setColor(Color.parseColor(loadAllocationModel.getOutput().getUndervoltageColor()));
                        }

                        if (breakerSectionId.get(loadAllocationModel.getOutput().getUndervoltage().get(i).getId()) != null) {
                            @SuppressLint("UseCompatLoadingForDrawables")
                            Drawable drawable = getResources().getDrawable(R.drawable.breaker);
                            Bitmap bitmap = drawableToBitmap(drawable);
                            Objects.requireNonNull(breakerSectionId.get(loadAllocationModel.getOutput().getUndervoltage().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(loadAllocationModel.getOutput().getUndervoltageColor())), 0, 0)));
                        }

                        if (transformerSectionId.get(loadAllocationModel.getOutput().getUndervoltage().get(i).getId()) != null) {
                            @SuppressLint("UseCompatLoadingForDrawables")
                            Drawable drawable = getResources().getDrawable(R.drawable.transformer);
                            Bitmap bitmap = drawableToBitmap(drawable);
                            Objects.requireNonNull(transformerSectionId.get(loadAllocationModel.getOutput().getUndervoltage().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(loadAllocationModel.getOutput().getUndervoltageColor())), 93, 0)));
                        }

                        if (fuseSectionId.get(loadAllocationModel.getOutput().getUndervoltage().get(i).getId()) != null) {
                            Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "L", 90f, Color.BLACK);
                            Objects.requireNonNull(fuseSectionId.get(loadAllocationModel.getOutput().getUndervoltage().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(loadAllocationModel.getOutput().getUndervoltageColor())), 0, 0)));
                        }

                        if (switchSectionId.get(loadAllocationModel.getOutput().getUndervoltage().get(i).getId()) != null) {
                            Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "D", 70f, Color.BLACK);
                            int paddingLeft = 0; // Left padding in pixels
                            int paddingTop = 0; // Top padding in pixels
                            int paddingRight = 0; // Right padding in pixels
                            int paddingBottom = 12; // Bottom padding in pixels
                            int newWidth = bitmap.getWidth() + paddingLeft + paddingRight;
                            int newHeight = bitmap.getHeight() + paddingTop + paddingBottom;
                            Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, bitmap.getConfig());
                            Canvas canva = new Canvas(paddedBitmap);
                            canva.drawBitmap(bitmap, paddingLeft, paddingTop, null);
                            Objects.requireNonNull(switchSectionId.get(loadAllocationModel.getOutput().getUndervoltage().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(loadAllocationModel.getOutput().getUndervoltageColor())), 0, 0)));
                        }

                        if (capacitorSectionId.get(loadAllocationModel.getOutput().getUndervoltage().get(i).getId()) != null) {
                            Objects.requireNonNull(capacitorSectionId.get(loadAllocationModel.getOutput().getUndervoltage().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(loadAllocationModel.getOutput().getUndervoltageColor())), 90, 0)));
                        }

                        if (spotLoadSectionId.get(loadAllocationModel.getOutput().getUndervoltage().get(i).getId()) != null) {
                            if (spLineSectionList.contains(loadAllocationModel.getOutput().getUndervoltage().get(i).getId())) {
                                int paddingPx = 0;
                                Bitmap paddedBitmap = Bitmap.createBitmap(BitmapImg.Spotload().getWidth() + paddingPx * 2, // Add padding to both sides
                                        BitmapImg.Spotload().getHeight() + paddingPx * 2, Bitmap.Config.ARGB_8888);
                                Canvas canvas1 = new Canvas(paddedBitmap);
                                canvas1.drawBitmap(BitmapImg.Spotload(), paddingPx, 10, null);
                                Objects.requireNonNull(spotLoadSectionId.get(loadAllocationModel.getOutput().getUndervoltage().get(i).getId())).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(loadAllocationModel.getOutput().getUndervoltageColor())), 0, 0)));
                            } else {
                                spotLoadSectionId.get(loadAllocationModel.getOutput().getUndervoltage().get(i).getId()).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Spotload(), Color.parseColor(loadAllocationModel.getOutput().getUndervoltageColor())), 0, 35)));
                            }
                        }

                        if (!secNodeSectionId.isEmpty()) {
                            if (secNodeSectionId.get(loadAllocationModel.getOutput().getUndervoltage().get(i).getId()) != null) {
                                Objects.requireNonNull(secNodeSectionId.get(loadAllocationModel.getOutput().getUndervoltage().get(i).getId())).getPaint().setColor(Color.parseColor(loadAllocationModel.getOutput().getUndervoltageColor()));
                            }
                        }
                    }
                }

            } catch (Exception e) {
                Config.isLoadAllocation = false;
                Status = "Load Allocation Analysis Failed";
                Log.d("Exception", e.getLocalizedMessage());
            }
            return Status;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("Load Allocation Analysis Complete")) {
                Config.isLoadAllocation = true;
                binding.reportBtn.setVisibility(View.VISIBLE);
                Snackbar snack = Snackbar.make(findViewById(android.R.id.content), s, Snackbar.LENGTH_LONG);
                snack.show();
            } else {
                Config.isLoadAllocation = false;
                binding.reportBtn.setVisibility(View.GONE);
                Snackbar snack = Snackbar.make(findViewById(android.R.id.content), s + " " + "Please Try Again Later", Snackbar.LENGTH_LONG);
                snack.show();
            }
            binding.map.invalidate();
        }

    }

    @SuppressLint("StaticFieldLeak")
    private class TraceDevices extends AsyncTask<Void, Void, String> {

        private final Tracing tracing;
        private final String type;

        public TraceDevices(Tracing tracing, String type) {
            this.tracing = tracing;
            this.type = type;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBarLayout.setVisibility(View.VISIBLE);
        }

        @SuppressLint({"ResourceAsColor", "WrongThread"})
        @Override
        protected String doInBackground(Void... voids) {
            try {

                if (Config.isLoadFlow) {
                    if (CaPolylineList != null && !CaPolylineList.isEmpty()) {
                        for (int j = 0; j < CaPolylineList.size(); j++) {
                            CaPolylineList.get(j).getPaint().setColor(Color.BLACK);
                        }
                    }

                    if (ohPolylineList != null && !ohPolylineList.isEmpty()) {
                        for (int j = 0; j < ohPolylineList.size(); j++) {
                            ohPolylineList.get(j).getPaint().setColor(Color.BLACK);
                        }
                    }

                    if (unBalPolylineList != null && !unBalPolylineList.isEmpty()) {
                        for (int j = 0; j < unBalPolylineList.size(); j++) {
                            unBalPolylineList.get(j).getPaint().setColor(Color.BLACK);
                        }
                    }

                    if (secNodeList != null && !secNodeList.isEmpty()) {
                        for (int j = 0; j < secNodeList.size(); j++) {
                            secNodeList.get(j).getPaint().setColor(Color.BLACK);
                        }
                    }

                    if (breakerList != null && !breakerList.isEmpty()) {
                        for (int i = 0; i < breakerList.size(); i++) {
                            @SuppressLint("UseCompatLoadingForDrawables")
                            Drawable drawable = getResources().getDrawable(R.drawable.breaker);
                            Bitmap bitmap = drawableToBitmap(drawable);
                            breakerList.get(i).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 95)));
                        }
                    }

                    if (transformerList != null && !transformerList.isEmpty()) {
                        for (int i = 0; i < transformerList.size(); i++) {
                            @SuppressLint("UseCompatLoadingForDrawables")
                            Drawable drawable = getResources().getDrawable(R.drawable.transformer);
                            Bitmap bitmap = drawableToBitmap(drawable);
                            transformerList.get(i).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 93)));
                        }
                    }

                    if (fuseList != null && !fuseList.isEmpty()) {
                        for (int i = 0; i < fuseList.size(); i++) {
                            Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "L", 90f, Color.BLACK);
                            fuseList.get(i).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 0)));
                        }
                    }

                    if (switchedList != null && !switchedList.isEmpty()) {
                        for (int i = 0; i < switchedList.size(); i++) {
                            Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "D", 70f, Color.BLACK);
                            int paddingLeft = 0; // Left padding in pixels
                            int paddingTop = 0; // Top padding in pixels
                            int paddingRight = 0; // Right padding in pixels
                            int paddingBottom = 12; // Bottom padding in pixels
                            int newWidth = bitmap.getWidth() + paddingLeft + paddingRight;
                            int newHeight = bitmap.getHeight() + paddingTop + paddingBottom;
                            Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, bitmap.getConfig());
                            Canvas canva = new Canvas(paddedBitmap);
                            canva.drawBitmap(bitmap, paddingLeft, paddingTop, null);
                            switchedList.get(i).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.BLACK), 0, 0)));
                        }
                    }

                    if (capacitorList != null && !capacitorList.isEmpty()) {
                        for (int i = 0; i < capacitorList.size(); i++) {
                            capacitorList.get(i).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.BLACK), 90, 0)));
                        }
                    }

                    if (spotLoadList != null && !spotLoadList.isEmpty()) {
                        for (int i = 0; i < spotLoadList.size(); i++) {
                            int paddingPx = 0;
                            Bitmap paddedBitmap = Bitmap.createBitmap(BitmapImg.Spotload().getWidth() + paddingPx * 2, // Add padding to both sides
                                    BitmapImg.Spotload().getHeight() + paddingPx * 2, Bitmap.Config.ARGB_8888);
                            Canvas canvas1 = new Canvas(paddedBitmap);
                            canvas1.drawBitmap(BitmapImg.Spotload(), paddingPx, 10, null);
                            spotLoadList.get(i).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.BLACK), 0, 0)));
                        }
                    }

                    if (loadFlowOverVoltageSectionId != null && overVoltageColors != null && !loadFlowOverVoltageSectionId.isEmpty()) {
                        for (int i = 0; i < loadFlowOverVoltageSectionId.size(); i++) {

                            if (CaSectionId.containsKey(loadFlowOverVoltageSectionId.get(i))) {
                                Objects.requireNonNull(CaSectionId.get(loadFlowOverVoltageSectionId.get(i))).setColor(Color.parseColor(overVoltageColors));
                            }

                            if (OhSectionId.containsKey(loadFlowOverVoltageSectionId.get(i))) {
                                Objects.requireNonNull(OhSectionId.get(loadFlowOverVoltageSectionId.get(i))).setColor(Color.parseColor(overVoltageColors));
                            }

                            if (UnBalSectionId.containsKey(loadFlowOverVoltageSectionId.get(i))) {
                                Objects.requireNonNull(UnBalSectionId.get(loadFlowOverVoltageSectionId.get(i))).setColor(Color.parseColor(overVoltageColors));
                            }

                            if (breakerSectionId.containsKey(loadFlowOverVoltageSectionId.get(i))) {
                                @SuppressLint("UseCompatLoadingForDrawables")
                                Drawable drawable = getResources().getDrawable(R.drawable.breaker);
                                Bitmap bitmap = drawableToBitmap(drawable);
                                Objects.requireNonNull(breakerSectionId.get(loadFlowOverVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 0, 95)));
                            }

                            if (transformerSectionId.containsKey(loadFlowOverVoltageSectionId.get(i))) {
                                @SuppressLint("UseCompatLoadingForDrawables")
                                Drawable drawable = getResources().getDrawable(R.drawable.transformer);
                                Bitmap bitmap = drawableToBitmap(drawable);
                                Objects.requireNonNull(transformerSectionId.get(loadFlowOverVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 0, 93)));
                            }

                            if (fuseSectionId.containsKey(loadFlowOverVoltageSectionId.get(i))) {
                                Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "L", 90f, Color.BLACK);
                                Objects.requireNonNull(fuseSectionId.get(loadFlowOverVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 0, 0)));
                            }

                            if (switchSectionId.containsKey(loadFlowOverVoltageSectionId.get(i))) {
                                Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "D", 70f, Color.BLACK);
                                int paddingLeft = 0; // Left padding in pixels
                                int paddingTop = 0; // Top padding in pixels
                                int paddingRight = 0; // Right padding in pixels
                                int paddingBottom = 12; // Bottom padding in pixels
                                int newWidth = bitmap.getWidth() + paddingLeft + paddingRight;
                                int newHeight = bitmap.getHeight() + paddingTop + paddingBottom;
                                Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, bitmap.getConfig());
                                Canvas canva = new Canvas(paddedBitmap);
                                canva.drawBitmap(bitmap, paddingLeft, paddingTop, null);
                                Objects.requireNonNull(switchSectionId.get(loadFlowOverVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(overVoltageColors)), 0, 0)));
                            }

                            if (capacitorSectionId.containsKey(loadFlowOverVoltageSectionId.get(i))) {
                                Objects.requireNonNull(capacitorSectionId.get(loadFlowOverVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(overVoltageColors)), 90, 0)));
                            }

                            if (spotLoadSectionId.containsKey(loadFlowOverVoltageSectionId.get(i))) {
                                if (spLineSectionList.contains(loadFlowOverVoltageSectionId.get(i))) {
                                    int paddingPx = 0;
                                    Bitmap paddedBitmap = Bitmap.createBitmap(BitmapImg.Spotload().getWidth() + paddingPx * 2, // Add padding to both sides
                                            BitmapImg.Spotload().getHeight() + paddingPx * 2, Bitmap.Config.ARGB_8888);
                                    Canvas canvas1 = new Canvas(paddedBitmap);
                                    canvas1.drawBitmap(BitmapImg.Spotload(), paddingPx, 10, null);
                                    Objects.requireNonNull(spotLoadSectionId.get(loadFlowOverVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(overVoltageColors)), 0, 0)));
                                } else {
                                    Objects.requireNonNull(spotLoadSectionId.get(loadFlowOverVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Spotload(), Color.parseColor(overVoltageColors)), 0, 35)));
                                }
                            }

                        }
                    }

                    if (loadFlowUnderVoltageSectionId != null && underVoltageColors != null && !loadFlowUnderVoltageSectionId.isEmpty()) {
                        for (int i = 0; i < loadFlowUnderVoltageSectionId.size(); i++) {

                            if (CaSectionId.containsKey(loadFlowUnderVoltageSectionId.get(i))) {
                                Objects.requireNonNull(CaSectionId.get(loadFlowUnderVoltageSectionId.get(i))).setColor(Color.parseColor(underVoltageColors));
                            }

                            if (OhSectionId.containsKey(loadFlowUnderVoltageSectionId.get(i))) {
                                Objects.requireNonNull(OhSectionId.get(loadFlowUnderVoltageSectionId.get(i))).setColor(Color.parseColor(underVoltageColors));
                            }

                            if (UnBalSectionId.containsKey(loadFlowUnderVoltageSectionId.get(i))) {
                                Objects.requireNonNull(UnBalSectionId.get(loadFlowUnderVoltageSectionId.get(i))).setColor(Color.parseColor(underVoltageColors));
                            }

                            if (breakerSectionId.containsKey(loadFlowUnderVoltageSectionId.get(i))) {
                                @SuppressLint("UseCompatLoadingForDrawables")
                                Drawable drawable = getResources().getDrawable(R.drawable.breaker);
                                Bitmap bitmap = drawableToBitmap(drawable);
                                Objects.requireNonNull(breakerSectionId.get(loadFlowUnderVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 0, 95)));
                            }

                            if (transformerSectionId.containsKey(loadFlowUnderVoltageSectionId.get(i))) {
                                @SuppressLint("UseCompatLoadingForDrawables")
                                Drawable drawable = getResources().getDrawable(R.drawable.transformer);
                                Bitmap bitmap = drawableToBitmap(drawable);
                                Objects.requireNonNull(transformerSectionId.get(loadFlowUnderVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 0, 93)));
                            }

                            if (fuseSectionId.containsKey(loadFlowUnderVoltageSectionId.get(i))) {
                                Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "L", 90f, Color.BLACK);
                                Objects.requireNonNull(fuseSectionId.get(loadFlowUnderVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 0, 0)));
                            }

                            if (switchSectionId.containsKey(loadFlowUnderVoltageSectionId.get(i))) {
                                Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "D", 70f, Color.BLACK);
                                int paddingLeft = 0; // Left padding in pixels
                                int paddingTop = 0; // Top padding in pixels
                                int paddingRight = 0; // Right padding in pixels
                                int paddingBottom = 12; // Bottom padding in pixels
                                int newWidth = bitmap.getWidth() + paddingLeft + paddingRight;
                                int newHeight = bitmap.getHeight() + paddingTop + paddingBottom;
                                Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, bitmap.getConfig());
                                Canvas canva = new Canvas(paddedBitmap);
                                canva.drawBitmap(bitmap, paddingLeft, paddingTop, null);
                                Objects.requireNonNull(switchSectionId.get(loadFlowUnderVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(underVoltageColors)), 0, 0)));
                            }

                            if (capacitorSectionId.containsKey(loadFlowUnderVoltageSectionId.get(i))) {
                                Objects.requireNonNull(capacitorSectionId.get(loadFlowUnderVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(underVoltageColors)), 90, 0)));
                            }

                            if (spotLoadSectionId.containsKey(loadFlowUnderVoltageSectionId.get(i))) {
                                if (spLineSectionList.contains(loadFlowUnderVoltageSectionId.get(i))) {
                                    int paddingPx = 0;
                                    Bitmap paddedBitmap = Bitmap.createBitmap(BitmapImg.Spotload().getWidth() + paddingPx * 2, // Add padding to both sides
                                            BitmapImg.Spotload().getHeight() + paddingPx * 2, Bitmap.Config.ARGB_8888);
                                    Canvas canvas1 = new Canvas(paddedBitmap);
                                    canvas1.drawBitmap(BitmapImg.Spotload(), paddingPx, 10, null);
                                    Objects.requireNonNull(spotLoadSectionId.get(loadFlowUnderVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(underVoltageColors)), 0, 0)));
                                } else {
                                    Objects.requireNonNull(spotLoadSectionId.get(loadFlowUnderVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Spotload(), Color.parseColor(underVoltageColors)), 0, 35)));
                                }
                            }

                        }
                    }

                    if (loadFlowOverLoadSectionId != null && overloadColors != null && !loadFlowOverLoadSectionId.isEmpty()) {
                        for (int i = 0; i < loadFlowOverLoadSectionId.size(); i++) {

                            if (CaSectionId.containsKey(loadFlowOverLoadSectionId.get(i))) {
                                Objects.requireNonNull(CaSectionId.get(loadFlowOverLoadSectionId.get(i))).setColor(Color.parseColor(overloadColors));
                            }

                            if (OhSectionId.containsKey(loadFlowOverLoadSectionId.get(i))) {
                                Objects.requireNonNull(OhSectionId.get(loadFlowOverLoadSectionId.get(i))).setColor(Color.parseColor(overloadColors));
                            }

                            if (UnBalSectionId.containsKey(loadFlowOverLoadSectionId.get(i))) {
                                Objects.requireNonNull(UnBalSectionId.get(loadFlowOverLoadSectionId.get(i))).setColor(Color.parseColor(overloadColors));
                            }

                            if (breakerSectionId.containsKey(loadFlowOverLoadSectionId.get(i))) {
                                @SuppressLint("UseCompatLoadingForDrawables")
                                Drawable drawable = getResources().getDrawable(R.drawable.breaker);
                                Bitmap bitmap = drawableToBitmap(drawable);
                                Objects.requireNonNull(breakerSectionId.get(loadFlowOverLoadSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 0, 95)));
                            }

                            if (transformerSectionId.containsKey(loadFlowOverLoadSectionId.get(i))) {
                                @SuppressLint("UseCompatLoadingForDrawables")
                                Drawable drawable = getResources().getDrawable(R.drawable.transformer);
                                Bitmap bitmap = drawableToBitmap(drawable);
                                Objects.requireNonNull(transformerSectionId.get(loadFlowOverLoadSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 0, 93)));
                            }

                            if (fuseSectionId.containsKey(loadFlowOverLoadSectionId.get(i))) {
                                Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "L", 90f, Color.BLACK);
                                Objects.requireNonNull(fuseSectionId.get(loadFlowOverLoadSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 0, 0)));
                            }

                            if (switchSectionId.containsKey(loadFlowOverLoadSectionId.get(i))) {
                                Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "D", 70f, Color.BLACK);
                                int paddingLeft = 0; // Left padding in pixels
                                int paddingTop = 0; // Top padding in pixels
                                int paddingRight = 0; // Right padding in pixels
                                int paddingBottom = 12; // Bottom padding in pixels
                                int newWidth = bitmap.getWidth() + paddingLeft + paddingRight;
                                int newHeight = bitmap.getHeight() + paddingTop + paddingBottom;
                                Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, bitmap.getConfig());
                                Canvas canva = new Canvas(paddedBitmap);
                                canva.drawBitmap(bitmap, paddingLeft, paddingTop, null);
                                Objects.requireNonNull(switchSectionId.get(loadFlowOverLoadSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(overloadColors)), 0, 0)));
                            }

                            if (capacitorSectionId.containsKey(loadFlowOverLoadSectionId.get(i))) {
                                Objects.requireNonNull(capacitorSectionId.get(loadFlowOverLoadSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(overloadColors)), 90, 0)));
                            }

                            if (spotLoadSectionId.containsKey(loadFlowOverLoadSectionId.get(i))) {
                                if (spLineSectionList.contains(loadFlowOverLoadSectionId.get(i))) {
                                    int paddingPx = 0;
                                    Bitmap paddedBitmap = Bitmap.createBitmap(BitmapImg.Spotload().getWidth() + paddingPx * 2, // Add padding to both sides
                                            BitmapImg.Spotload().getHeight() + paddingPx * 2, Bitmap.Config.ARGB_8888);
                                    Canvas canvas1 = new Canvas(paddedBitmap);
                                    canvas1.drawBitmap(BitmapImg.Spotload(), paddingPx, 10, null);
                                    Objects.requireNonNull(spotLoadSectionId.get(loadFlowOverLoadSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(overloadColors)), 0, 0)));
                                } else {
                                    Objects.requireNonNull(spotLoadSectionId.get(loadFlowOverLoadSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Spotload(), Color.parseColor(overloadColors)), 0, 35)));
                                }
                            }

                        }
                    }
                } else if (Config.isShortCircuit) {
                    if (CaPolylineList != null && !CaPolylineList.isEmpty()) {
                        for (int j = 0; j < CaPolylineList.size(); j++) {
                            CaPolylineList.get(j).getPaint().setColor(Color.BLACK);
                        }
                    }

                    if (ohPolylineList != null && !ohPolylineList.isEmpty()) {
                        for (int j = 0; j < ohPolylineList.size(); j++) {
                            ohPolylineList.get(j).getPaint().setColor(Color.BLACK);
                        }
                    }

                    if (unBalPolylineList != null && !unBalPolylineList.isEmpty()) {
                        for (int j = 0; j < unBalPolylineList.size(); j++) {
                            unBalPolylineList.get(j).getPaint().setColor(Color.BLACK);
                        }
                    }

                    if (secNodeList != null && !secNodeList.isEmpty()) {
                        for (int j = 0; j < secNodeList.size(); j++) {
                            secNodeList.get(j).getPaint().setColor(Color.BLACK);
                        }
                    }

                    if (breakerList != null && !breakerList.isEmpty()) {
                        for (int i = 0; i < breakerList.size(); i++) {
                            @SuppressLint("UseCompatLoadingForDrawables")
                            Drawable drawable = getResources().getDrawable(R.drawable.breaker);
                            Bitmap bitmap = drawableToBitmap(drawable);
                            breakerList.get(i).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 95)));
                        }
                    }

                    if (transformerList != null && !transformerList.isEmpty()) {
                        for (int i = 0; i < transformerList.size(); i++) {
                            @SuppressLint("UseCompatLoadingForDrawables")
                            Drawable drawable = getResources().getDrawable(R.drawable.transformer);
                            Bitmap bitmap = drawableToBitmap(drawable);
                            transformerList.get(i).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 93)));
                        }
                    }

                    if (fuseList != null && !fuseList.isEmpty()) {
                        for (int i = 0; i < fuseList.size(); i++) {
                            Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "L", 90f, Color.BLACK);
                            fuseList.get(i).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.BLACK), 0, 0)));
                        }
                    }

                    if (switchedList != null && !switchedList.isEmpty()) {
                        for (int i = 0; i < switchedList.size(); i++) {
                            Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "D", 70f, Color.BLACK);
                            int paddingLeft = 0; // Left padding in pixels
                            int paddingTop = 0; // Top padding in pixels
                            int paddingRight = 0; // Right padding in pixels
                            int paddingBottom = 12; // Bottom padding in pixels
                            int newWidth = bitmap.getWidth() + paddingLeft + paddingRight;
                            int newHeight = bitmap.getHeight() + paddingTop + paddingBottom;
                            Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, bitmap.getConfig());
                            Canvas canva = new Canvas(paddedBitmap);
                            canva.drawBitmap(bitmap, paddingLeft, paddingTop, null);
                            switchedList.get(i).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.BLACK), 0, 0)));
                        }
                    }

                    if (capacitorList != null && !capacitorList.isEmpty()) {
                        for (int i = 0; i < capacitorList.size(); i++) {
                            capacitorList.get(i).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.BLACK), 90, 0)));
                        }
                    }

                    if (spotLoadList != null && !spotLoadList.isEmpty()) {
                        for (int i = 0; i < spotLoadList.size(); i++) {
                            int paddingPx = 0;
                            Bitmap paddedBitmap = Bitmap.createBitmap(BitmapImg.Spotload().getWidth() + paddingPx * 2, // Add padding to both sides
                                    BitmapImg.Spotload().getHeight() + paddingPx * 2, Bitmap.Config.ARGB_8888);
                            Canvas canvas1 = new Canvas(paddedBitmap);
                            canvas1.drawBitmap(BitmapImg.Spotload(), paddingPx, 10, null);
                            spotLoadList.get(i).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.BLACK), 0, 0)));
                        }
                    }

                    if (shortCircuitRatingSectionId != null && ratingColors != null && !shortCircuitRatingSectionId.isEmpty()) {
                        for (int i = 0; i < shortCircuitRatingSectionId.size(); i++) {

                            if (CaSectionId.containsKey(shortCircuitRatingSectionId.get(i))) {
                                Objects.requireNonNull(CaSectionId.get(shortCircuitRatingSectionId.get(i))).setColor(Color.parseColor(ratingColors));
                            }

                            if (OhSectionId.containsKey(shortCircuitRatingSectionId.get(i))) {
                                Objects.requireNonNull(OhSectionId.get(shortCircuitRatingSectionId.get(i))).setColor(Color.parseColor(ratingColors));
                            }

                            if (UnBalSectionId.containsKey(shortCircuitRatingSectionId.get(i))) {
                                Objects.requireNonNull(UnBalSectionId.get(shortCircuitRatingSectionId.get(i))).setColor(Color.parseColor(ratingColors));
                            }

                            if (breakerSectionId.containsKey(shortCircuitRatingSectionId.get(i))) {
                                @SuppressLint("UseCompatLoadingForDrawables")
                                Drawable drawable = getResources().getDrawable(R.drawable.breaker);
                                Bitmap bitmap = drawableToBitmap(drawable);
                                Objects.requireNonNull(breakerSectionId.get(shortCircuitRatingSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(ratingColors)), 0, 95)));
                            }

                            if (transformerSectionId.containsKey(shortCircuitRatingSectionId.get(i))) {
                                @SuppressLint("UseCompatLoadingForDrawables")
                                Drawable drawable = getResources().getDrawable(R.drawable.transformer);
                                Bitmap bitmap = drawableToBitmap(drawable);
                                Objects.requireNonNull(transformerSectionId.get(shortCircuitRatingSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(ratingColors)), 0, 93)));
                            }

                            if (fuseSectionId.containsKey(shortCircuitRatingSectionId.get(i))) {
                                Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "L", 90f, Color.BLACK);
                                Objects.requireNonNull(fuseSectionId.get(shortCircuitRatingSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(ratingColors)), 0, 0)));
                            }

                            if (switchSectionId.containsKey(shortCircuitRatingSectionId.get(i))) {
                                Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "D", 70f, Color.BLACK);
                                int paddingLeft = 0; // Left padding in pixels
                                int paddingTop = 0; // Top padding in pixels
                                int paddingRight = 0; // Right padding in pixels
                                int paddingBottom = 12; // Bottom padding in pixels
                                int newWidth = bitmap.getWidth() + paddingLeft + paddingRight;
                                int newHeight = bitmap.getHeight() + paddingTop + paddingBottom;
                                Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, bitmap.getConfig());
                                Canvas canva = new Canvas(paddedBitmap);
                                canva.drawBitmap(bitmap, paddingLeft, paddingTop, null);
                                Objects.requireNonNull(switchSectionId.get(shortCircuitRatingSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(ratingColors)), 0, 0)));
                            }

                            if (capacitorSectionId.containsKey(shortCircuitRatingSectionId.get(i))) {
                                Objects.requireNonNull(capacitorSectionId.get(shortCircuitRatingSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(ratingColors)), 90, 0)));
                            }

                            if (spotLoadSectionId.containsKey(shortCircuitRatingSectionId.get(i))) {
                                if (spLineSectionList.contains(shortCircuitRatingSectionId.get(i))) {
                                    int paddingPx = 0;
                                    Bitmap paddedBitmap = Bitmap.createBitmap(BitmapImg.Spotload().getWidth() + paddingPx * 2, // Add padding to both sides
                                            BitmapImg.Spotload().getHeight() + paddingPx * 2, Bitmap.Config.ARGB_8888);
                                    Canvas canvas1 = new Canvas(paddedBitmap);
                                    canvas1.drawBitmap(BitmapImg.Spotload(), paddingPx, 10, null);
                                    Objects.requireNonNull(spotLoadSectionId.get(shortCircuitRatingSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(ratingColors)), 0, 0)));
                                } else {
                                    Objects.requireNonNull(spotLoadSectionId.get(shortCircuitRatingSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Spotload(), Color.parseColor(ratingColors)), 0, 35)));
                                }
                            }

                        }
                    }

                    if (shortCircuitOverVoltageSectionId != null && overVoltageColors != null && !shortCircuitOverVoltageSectionId.isEmpty()) {
                        for (int i = 0; i < shortCircuitOverVoltageSectionId.size(); i++) {

                            if (CaSectionId.containsKey(shortCircuitOverVoltageSectionId.get(i))) {
                                Objects.requireNonNull(CaSectionId.get(shortCircuitOverVoltageSectionId.get(i))).setColor(Color.parseColor(overVoltageColors));
                            }

                            if (OhSectionId.containsKey(shortCircuitOverVoltageSectionId.get(i))) {
                                Objects.requireNonNull(OhSectionId.get(shortCircuitOverVoltageSectionId.get(i))).setColor(Color.parseColor(overVoltageColors));
                            }

                            if (UnBalSectionId.containsKey(shortCircuitOverVoltageSectionId.get(i))) {
                                Objects.requireNonNull(UnBalSectionId.get(shortCircuitOverVoltageSectionId.get(i))).setColor(Color.parseColor(overVoltageColors));
                            }

                            if (breakerSectionId.containsKey(shortCircuitOverVoltageSectionId.get(i))) {
                                @SuppressLint("UseCompatLoadingForDrawables")
                                Drawable drawable = getResources().getDrawable(R.drawable.breaker);
                                Bitmap bitmap = drawableToBitmap(drawable);
                                Objects.requireNonNull(breakerSectionId.get(shortCircuitOverVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 0, 95)));
                            }

                            if (transformerSectionId.containsKey(shortCircuitOverVoltageSectionId.get(i))) {
                                @SuppressLint("UseCompatLoadingForDrawables")
                                Drawable drawable = getResources().getDrawable(R.drawable.transformer);
                                Bitmap bitmap = drawableToBitmap(drawable);
                                Objects.requireNonNull(transformerSectionId.get(shortCircuitOverVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 0, 93)));
                            }

                            if (fuseSectionId.containsKey(shortCircuitOverVoltageSectionId.get(i))) {
                                Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "L", 90f, Color.BLACK);
                                Objects.requireNonNull(fuseSectionId.get(shortCircuitOverVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overVoltageColors)), 0, 0)));
                            }

                            if (switchSectionId.containsKey(shortCircuitOverVoltageSectionId.get(i))) {
                                Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "D", 70f, Color.BLACK);
                                int paddingLeft = 0; // Left padding in pixels
                                int paddingTop = 0; // Top padding in pixels
                                int paddingRight = 0; // Right padding in pixels
                                int paddingBottom = 12; // Bottom padding in pixels
                                int newWidth = bitmap.getWidth() + paddingLeft + paddingRight;
                                int newHeight = bitmap.getHeight() + paddingTop + paddingBottom;
                                Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, bitmap.getConfig());
                                Canvas canva = new Canvas(paddedBitmap);
                                canva.drawBitmap(bitmap, paddingLeft, paddingTop, null);
                                Objects.requireNonNull(switchSectionId.get(shortCircuitOverVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(overVoltageColors)), 0, 0)));
                            }

                            if (capacitorSectionId.containsKey(shortCircuitOverVoltageSectionId.get(i))) {
                                Objects.requireNonNull(capacitorSectionId.get(shortCircuitOverVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(overVoltageColors)), 90, 0)));
                            }

                            if (spotLoadSectionId.containsKey(shortCircuitOverVoltageSectionId.get(i))) {
                                if (spLineSectionList.contains(shortCircuitOverVoltageSectionId.get(i))) {
                                    int paddingPx = 0;
                                    Bitmap paddedBitmap = Bitmap.createBitmap(BitmapImg.Spotload().getWidth() + paddingPx * 2, // Add padding to both sides
                                            BitmapImg.Spotload().getHeight() + paddingPx * 2, Bitmap.Config.ARGB_8888);
                                    Canvas canvas1 = new Canvas(paddedBitmap);
                                    canvas1.drawBitmap(BitmapImg.Spotload(), paddingPx, 10, null);
                                    Objects.requireNonNull(spotLoadSectionId.get(shortCircuitOverVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(overVoltageColors)), 0, 0)));
                                } else {
                                    Objects.requireNonNull(spotLoadSectionId.get(shortCircuitOverVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Spotload(), Color.parseColor(overVoltageColors)), 0, 35)));
                                }
                            }

                        }
                    }

                    if (shortCircuitUnderVoltageSectionId != null && underVoltageColors != null && !shortCircuitUnderVoltageSectionId.isEmpty()) {
                        for (int i = 0; i < shortCircuitUnderVoltageSectionId.size(); i++) {

                            if (CaSectionId.containsKey(shortCircuitUnderVoltageSectionId.get(i))) {
                                Objects.requireNonNull(CaSectionId.get(shortCircuitUnderVoltageSectionId.get(i))).setColor(Color.parseColor(underVoltageColors));
                            }

                            if (OhSectionId.containsKey(shortCircuitUnderVoltageSectionId.get(i))) {
                                Objects.requireNonNull(OhSectionId.get(shortCircuitUnderVoltageSectionId.get(i))).setColor(Color.parseColor(underVoltageColors));
                            }

                            if (UnBalSectionId.containsKey(shortCircuitUnderVoltageSectionId.get(i))) {
                                Objects.requireNonNull(UnBalSectionId.get(shortCircuitUnderVoltageSectionId.get(i))).setColor(Color.parseColor(underVoltageColors));
                            }

                            if (breakerSectionId.containsKey(shortCircuitUnderVoltageSectionId.get(i))) {
                                @SuppressLint("UseCompatLoadingForDrawables")
                                Drawable drawable = getResources().getDrawable(R.drawable.breaker);
                                Bitmap bitmap = drawableToBitmap(drawable);
                                Objects.requireNonNull(breakerSectionId.get(shortCircuitUnderVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 0, 95)));
                            }

                            if (transformerSectionId.containsKey(shortCircuitUnderVoltageSectionId.get(i))) {
                                @SuppressLint("UseCompatLoadingForDrawables")
                                Drawable drawable = getResources().getDrawable(R.drawable.transformer);
                                Bitmap bitmap = drawableToBitmap(drawable);
                                Objects.requireNonNull(transformerSectionId.get(shortCircuitUnderVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 0, 93)));
                            }

                            if (fuseSectionId.containsKey(shortCircuitUnderVoltageSectionId.get(i))) {
                                Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "L", 90f, Color.BLACK);
                                Objects.requireNonNull(fuseSectionId.get(shortCircuitUnderVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(underVoltageColors)), 0, 0)));
                            }

                            if (switchSectionId.containsKey(shortCircuitUnderVoltageSectionId.get(i))) {
                                Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "D", 70f, Color.BLACK);
                                int paddingLeft = 0; // Left padding in pixels
                                int paddingTop = 0; // Top padding in pixels
                                int paddingRight = 0; // Right padding in pixels
                                int paddingBottom = 12; // Bottom padding in pixels
                                int newWidth = bitmap.getWidth() + paddingLeft + paddingRight;
                                int newHeight = bitmap.getHeight() + paddingTop + paddingBottom;
                                Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, bitmap.getConfig());
                                Canvas canva = new Canvas(paddedBitmap);
                                canva.drawBitmap(bitmap, paddingLeft, paddingTop, null);
                                Objects.requireNonNull(switchSectionId.get(shortCircuitUnderVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(underVoltageColors)), 0, 0)));
                            }

                            if (capacitorSectionId.containsKey(shortCircuitUnderVoltageSectionId.get(i))) {
                                Objects.requireNonNull(capacitorSectionId.get(shortCircuitUnderVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(underVoltageColors)), 90, 0)));
                            }

                            if (spotLoadSectionId.containsKey(shortCircuitUnderVoltageSectionId.get(i))) {
                                if (spLineSectionList.contains(shortCircuitUnderVoltageSectionId.get(i))) {
                                    int paddingPx = 0;
                                    Bitmap paddedBitmap = Bitmap.createBitmap(BitmapImg.Spotload().getWidth() + paddingPx * 2, // Add padding to both sides
                                            BitmapImg.Spotload().getHeight() + paddingPx * 2, Bitmap.Config.ARGB_8888);
                                    Canvas canvas1 = new Canvas(paddedBitmap);
                                    canvas1.drawBitmap(BitmapImg.Spotload(), paddingPx, 10, null);
                                    Objects.requireNonNull(spotLoadSectionId.get(shortCircuitUnderVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(underVoltageColors)), 0, 0)));
                                } else {
                                    Objects.requireNonNull(spotLoadSectionId.get(shortCircuitUnderVoltageSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Spotload(), Color.parseColor(underVoltageColors)), 0, 35)));
                                }
                            }

                        }
                    }

                    if (shortCircuitOverLoadSectionId != null && overloadColors != null && !shortCircuitOverLoadSectionId.isEmpty()) {
                        for (int i = 0; i < shortCircuitOverLoadSectionId.size(); i++) {

                            if (CaSectionId.containsKey(shortCircuitOverLoadSectionId.get(i))) {
                                Objects.requireNonNull(CaSectionId.get(shortCircuitOverLoadSectionId.get(i))).setColor(Color.parseColor(overloadColors));
                            }

                            if (OhSectionId.containsKey(shortCircuitOverLoadSectionId.get(i))) {
                                Objects.requireNonNull(OhSectionId.get(shortCircuitOverLoadSectionId.get(i))).setColor(Color.parseColor(overloadColors));
                            }

                            if (UnBalSectionId.containsKey(shortCircuitOverLoadSectionId.get(i))) {
                                Objects.requireNonNull(UnBalSectionId.get(shortCircuitOverLoadSectionId.get(i))).setColor(Color.parseColor(overloadColors));
                            }

                            if (breakerSectionId.containsKey(shortCircuitOverLoadSectionId.get(i))) {
                                @SuppressLint("UseCompatLoadingForDrawables")
                                Drawable drawable = getResources().getDrawable(R.drawable.breaker);
                                Bitmap bitmap = drawableToBitmap(drawable);
                                Objects.requireNonNull(breakerSectionId.get(shortCircuitOverLoadSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 0, 95)));
                            }

                            if (transformerSectionId.containsKey(shortCircuitOverLoadSectionId.get(i))) {
                                @SuppressLint("UseCompatLoadingForDrawables")
                                Drawable drawable = getResources().getDrawable(R.drawable.transformer);
                                Bitmap bitmap = drawableToBitmap(drawable);
                                Objects.requireNonNull(transformerSectionId.get(shortCircuitOverLoadSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 0, 93)));
                            }

                            if (fuseSectionId.containsKey(shortCircuitOverLoadSectionId.get(i))) {
                                Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "L", 90f, Color.BLACK);
                                Objects.requireNonNull(fuseSectionId.get(shortCircuitOverLoadSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.parseColor(overloadColors)), 0, 0)));
                            }

                            if (switchSectionId.containsKey(shortCircuitOverLoadSectionId.get(i))) {
                                Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "D", 70f, Color.BLACK);
                                int paddingLeft = 0; // Left padding in pixels
                                int paddingTop = 0; // Top padding in pixels
                                int paddingRight = 0; // Right padding in pixels
                                int paddingBottom = 12; // Bottom padding in pixels
                                int newWidth = bitmap.getWidth() + paddingLeft + paddingRight;
                                int newHeight = bitmap.getHeight() + paddingTop + paddingBottom;
                                Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, bitmap.getConfig());
                                Canvas canva = new Canvas(paddedBitmap);
                                canva.drawBitmap(bitmap, paddingLeft, paddingTop, null);
                                Objects.requireNonNull(switchSectionId.get(shortCircuitOverLoadSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.parseColor(overloadColors)), 0, 0)));
                            }

                            if (capacitorSectionId.containsKey(shortCircuitOverLoadSectionId.get(i))) {
                                Objects.requireNonNull(capacitorSectionId.get(shortCircuitOverLoadSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.parseColor(overloadColors)), 90, 0)));
                            }

                            if (spotLoadSectionId.containsKey(shortCircuitOverLoadSectionId.get(i))) {
                                if (spLineSectionList.contains(shortCircuitOverLoadSectionId.get(i))) {
                                    int paddingPx = 0;
                                    Bitmap paddedBitmap = Bitmap.createBitmap(BitmapImg.Spotload().getWidth() + paddingPx * 2, // Add padding to both sides
                                            BitmapImg.Spotload().getHeight() + paddingPx * 2, Bitmap.Config.ARGB_8888);
                                    Canvas canvas1 = new Canvas(paddedBitmap);
                                    canvas1.drawBitmap(BitmapImg.Spotload(), paddingPx, 10, null);
                                    Objects.requireNonNull(spotLoadSectionId.get(shortCircuitOverLoadSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.parseColor(overloadColors)), 0, 0)));
                                } else {
                                    Objects.requireNonNull(spotLoadSectionId.get(shortCircuitOverLoadSectionId.get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Spotload(), Color.parseColor(overloadColors)), 0, 35)));
                                }
                            }
                        }
                    }
                } else {
                    if (CaPolylineList != null && !CaPolylineList.isEmpty()) {
                        for (int j = 0; j < CaPolylineList.size(); j++) {
                            CaPolylineList.get(j).getPaint().setColor(Color.RED);
                        }
                    }

                    if (ohPolylineList != null && !ohPolylineList.isEmpty()) {
                        for (int j = 0; j < ohPolylineList.size(); j++) {
                            ohPolylineList.get(j).getPaint().setColor(Color.BLUE);
                        }
                    }

                    if (unBalPolylineList != null && !unBalPolylineList.isEmpty()) {
                        for (int j = 0; j < unBalPolylineList.size(); j++) {
                            unBalPolylineList.get(j).getPaint().setColor(Color.BLACK);
                        }
                    }

                    if (secNodeList != null && !secNodeList.isEmpty()) {
                        for (int j = 0; j < secNodeList.size(); j++) {
                            secNodeList.get(j).getPaint().setColor(Color.BLACK);
                        }
                    }
                }

                if (tracing.getOutput() != null && tracing.getOutput().size() > 0 && !tracing.getOutput().isEmpty()) {
                    isTracing = true;

                    for (int i = 0; i < tracing.getOutput().size(); i++) {

                        if (type.equals("upStream")) {
                            if (CaSectionId.get(tracing.getOutput().get(i)) != null) {
                                Objects.requireNonNull(CaSectionId.get(tracing.getOutput().get(i))).setColor(Color.CYAN);
                            }

                            if (OhSectionId.get(tracing.getOutput().get(i)) != null) {
                                Objects.requireNonNull(OhSectionId.get(tracing.getOutput().get(i))).setColor(Color.CYAN);
                            }

                            if (UnBalSectionId.get(tracing.getOutput().get(i)) != null) {
                                Objects.requireNonNull(UnBalSectionId.get(tracing.getOutput().get(i))).setColor(Color.CYAN);
                            }

                            if (breakerSectionId.get(tracing.getOutput().get(i)) != null) {
                                @SuppressLint("UseCompatLoadingForDrawables")
                                Drawable drawable = getResources().getDrawable(R.drawable.breaker);
                                Bitmap bitmap = drawableToBitmap(drawable);
                                Objects.requireNonNull(breakerSectionId.get(tracing.getOutput().get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.CYAN), 0, 95)));
                            }

                            if (transformerSectionId.get(tracing.getOutput().get(i)) != null) {
                                @SuppressLint("UseCompatLoadingForDrawables")
                                Drawable drawable = getResources().getDrawable(R.drawable.transformer);
                                Bitmap bitmap = drawableToBitmap(drawable);
                                Objects.requireNonNull(transformerSectionId.get(tracing.getOutput().get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.CYAN), 0, 93)));
                            }

                            if (fuseSectionId.get(tracing.getOutput().get(i)) != null) {
                                Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "L", 90f, Color.BLACK);
                                Objects.requireNonNull(fuseSectionId.get(tracing.getOutput().get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.CYAN), 0, 0)));
                            }

                            if (switchSectionId.get(tracing.getOutput().get(i)) != null) {
                                Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "D", 70f, Color.BLACK);
                                int paddingLeft = 0; // Left padding in pixels
                                int paddingTop = 0; // Top padding in pixels
                                int paddingRight = 0; // Right padding in pixels
                                int paddingBottom = 12; // Bottom padding in pixels
                                int newWidth = bitmap.getWidth() + paddingLeft + paddingRight;
                                int newHeight = bitmap.getHeight() + paddingTop + paddingBottom;
                                Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, bitmap.getConfig());
                                Canvas canva = new Canvas(paddedBitmap);
                                canva.drawBitmap(bitmap, paddingLeft, paddingTop, null);
                                Objects.requireNonNull(switchSectionId.get(tracing.getOutput().get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.CYAN), 0, 0)));
                            }

                            if (capacitorSectionId.get(tracing.getOutput().get(i)) != null) {
                                Objects.requireNonNull(capacitorSectionId.get(tracing.getOutput().get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.CYAN), 90, 0)));
                            }

                            if (spotLoadSectionId.get(tracing.getOutput().get(i)) != null) {
                                if (spLineSectionList.contains(tracing.getOutput().get(i))) {
                                    int paddingPx = 0;
                                    Bitmap paddedBitmap = Bitmap.createBitmap(BitmapImg.Spotload().getWidth() + paddingPx * 2, // Add padding to both sides
                                            BitmapImg.Spotload().getHeight() + paddingPx * 2, Bitmap.Config.ARGB_8888);
                                    Canvas canvas1 = new Canvas(paddedBitmap);
                                    canvas1.drawBitmap(BitmapImg.Spotload(), paddingPx, 10, null);
                                    Objects.requireNonNull(spotLoadSectionId.get(tracing.getOutput().get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.CYAN), 0, 0)));
                                } else {
                                    Objects.requireNonNull(spotLoadSectionId.get(tracing.getOutput().get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Spotload(), Color.CYAN), 0, 35)));
                                }
                            }

                            if (!secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(tracing.getOutput().get(i)) != null) {
                                    Objects.requireNonNull(secNodeSectionId.get(tracing.getOutput().get(i))).getPaint().setColor(Color.CYAN);
                                }
                            }
                        } else {
                            if (CaSectionId.get(tracing.getOutput().get(i)) != null) {
                                Objects.requireNonNull(CaSectionId.get(tracing.getOutput().get(i))).setColor(Color.MAGENTA);
                            }

                            if (OhSectionId.get(tracing.getOutput().get(i)) != null) {
                                Objects.requireNonNull(OhSectionId.get(tracing.getOutput().get(i))).setColor(Color.MAGENTA);
                            }

                            if (UnBalSectionId.get(tracing.getOutput().get(i)) != null) {
                                Objects.requireNonNull(UnBalSectionId.get(tracing.getOutput().get(i))).setColor(Color.MAGENTA);
                            }

                            if (breakerSectionId.get(tracing.getOutput().get(i)) != null) {
                                @SuppressLint("UseCompatLoadingForDrawables")
                                Drawable drawable = getResources().getDrawable(R.drawable.breaker);
                                Bitmap bitmap = drawableToBitmap(drawable);
                                Objects.requireNonNull(breakerSectionId.get(tracing.getOutput().get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.MAGENTA), 0, 95)));
                            }

                            if (transformerSectionId.get(tracing.getOutput().get(i)) != null) {
                                @SuppressLint("UseCompatLoadingForDrawables")
                                Drawable drawable = getResources().getDrawable(R.drawable.transformer);
                                Bitmap bitmap = drawableToBitmap(drawable);
                                Objects.requireNonNull(transformerSectionId.get(tracing.getOutput().get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.MAGENTA), 0, 93)));
                            }

                            if (fuseSectionId.get(tracing.getOutput().get(i)) != null) {
                                Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "L", 90f, Color.BLACK);
                                Objects.requireNonNull(fuseSectionId.get(tracing.getOutput().get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(bitmap, Color.MAGENTA), 0, 0)));
                            }

                            if (switchSectionId.get(tracing.getOutput().get(i)) != null) {
                                Bitmap bitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "D", 70f, Color.BLACK);
                                int paddingLeft = 0; // Left padding in pixels
                                int paddingTop = 0; // Top padding in pixels
                                int paddingRight = 0; // Right padding in pixels
                                int paddingBottom = 12; // Bottom padding in pixels
                                int newWidth = bitmap.getWidth() + paddingLeft + paddingRight;
                                int newHeight = bitmap.getHeight() + paddingTop + paddingBottom;
                                Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, bitmap.getConfig());
                                Canvas canva = new Canvas(paddedBitmap);
                                canva.drawBitmap(bitmap, paddingLeft, paddingTop, null);
                                Objects.requireNonNull(switchSectionId.get(tracing.getOutput().get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.MAGENTA), 0, 0)));
                            }

                            if (capacitorSectionId.get(tracing.getOutput().get(i)) != null) {
                                Objects.requireNonNull(capacitorSectionId.get(tracing.getOutput().get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.MAGENTA), 90, 0)));
                            }

                            if (spotLoadSectionId.get(tracing.getOutput().get(i)) != null) {
                                if (spLineSectionList.contains(tracing.getOutput().get(i))) {
                                    int paddingPx = 0;
                                    Bitmap paddedBitmap = Bitmap.createBitmap(BitmapImg.Spotload().getWidth() + paddingPx * 2, // Add padding to both sides
                                            BitmapImg.Spotload().getHeight() + paddingPx * 2, Bitmap.Config.ARGB_8888);
                                    Canvas canvas1 = new Canvas(paddedBitmap);
                                    canvas1.drawBitmap(BitmapImg.Spotload(), paddingPx, 10, null);
                                    Objects.requireNonNull(spotLoadSectionId.get(tracing.getOutput().get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.MAGENTA), 0, 0)));
                                } else {
                                    Objects.requireNonNull(spotLoadSectionId.get(tracing.getOutput().get(i))).setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Spotload(), Color.MAGENTA), 0, 35)));
                                }
                            }

                            if (!secNodeSectionId.isEmpty()) {
                                if (secNodeSectionId.get(tracing.getOutput().get(i)) != null) {
                                    Objects.requireNonNull(secNodeSectionId.get(tracing.getOutput().get(i))).getPaint().setColor(Color.MAGENTA);
                                }
                            }
                        }

                    }
                }

            } catch (Exception e) {
                Log.d("Exception", e.getLocalizedMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBarLayout.setVisibility(View.GONE);
            binding.map.invalidate();
        }

    }

    private class ExpandableDeviceAdapter extends BaseExpandableListAdapter {

        private final Context context;
        private final ArrayList<Continent> continentList;
        private final ArrayList<Continent> originalList;

        public ExpandableDeviceAdapter(Context context, ArrayList<Continent> continentList) {
            this.context = context;
            this.continentList = new ArrayList<Continent>();
            this.continentList.addAll(continentList);
            this.originalList = new ArrayList<Continent>();
            this.originalList.addAll(continentList);
        }

        @Override
        public int getGroupCount() {
            return continentList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            ArrayList<DType> countryList = continentList.get(groupPosition).getDeviceList();
            return countryList.size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return continentList.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            ArrayList<DType> countryList = continentList.get(groupPosition).getDeviceList();
            return countryList.get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            Continent continent = (Continent) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.row_first, null);
            }
            TextView listTitleTextView = convertView.findViewById(R.id.first_tv);
            listTitleTextView.setTypeface(null, Typeface.BOLD);
            listTitleTextView.setTextColor(MapActivity.this.getColor(R.color.blue));
            listTitleTextView.setText(continent.getName());
            return convertView;
        }

        @SuppressLint({"InflateParams", "UseCompatLoadingForDrawables"})
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            DType type = (DType) getChild(groupPosition, childPosition);
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.row_second, null);

            }
            TextView tv = convertView.findViewById(R.id.row_second_tv);
            TextView dTypeTv = convertView.findViewById(R.id.dType_tv);

            ImageView img = convertView.findViewById(R.id.arro_imageview);
            img.setVisibility(View.GONE);

            tv.setTypeface(null, Typeface.BOLD);
            tv.setText(type.getName());
            dTypeTv.setText(String.valueOf(type.getType()));
            tv.setOnClickListener(view -> {
                if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(MapActivity.this)) {
                    if (!tv.getText().toString().trim().isEmpty() && !tv.getText().toString().trim().equals("null") && !dTypeTv.getText().toString().trim().isEmpty() && !dTypeTv.getText().toString().trim().equals("null") && prefManager.getUserType() != null) {
                        getDevices(tv.getText().toString().trim().replaceAll("\\s*\\(.*?\\)", ""), dTypeTv.getText().toString());
                    } else {
                        Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Not Found LatLon", Snackbar.LENGTH_LONG);
                        snack.show();
                    }
                } else {
                    Snackbar.make(binding.getRoot(), R.string.no_internet_connection, Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(MapActivity.this)) {
                                binding.drawerLayout.closeDrawer(GravityCompat.START);
                                getDevices(tv.getText().toString().trim(), dTypeTv.getText().toString());
                            }
                        }
                    }).show();
                }
            });

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

        public void filterData(String query) {
            query = query.toLowerCase();
            continentList.clear();
            if (query.isEmpty()) {
                continentList.addAll(originalList);
            } else {
                for (Continent continent : originalList) {
                    ArrayList<DType> countryList = continent.getDeviceList();
                    ArrayList<DType> newList = new ArrayList<DType>();
                    for (DType country : countryList) {
                        if (country.getName().toLowerCase().contains(query)) {
                            newList.add(country);
                        }
                    }
                    if (newList.size() > 0) {
                        Continent nContinent = new Continent(continent.getName(), newList);
                        continentList.add(nContinent);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    private class FiltersAdapter extends ArrayAdapter<DeviceName> {

        private final Context context;
        private final List<DeviceName> items;
        private final List<DeviceName> tempItems;
        private final List<DeviceName> suggestions;
        Filter nameFilter = new Filter() {
            @Override
            public CharSequence convertResultToString(Object resultValue) {
                return ((DeviceName) resultValue).getName();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (constraint != null) {
                    suggestions.clear();
                    for (DeviceName people : tempItems) {
                        if (people.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            suggestions.add(people);
                        }
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = suggestions;
                    filterResults.count = suggestions.size();
                    return filterResults;
                } else {
                    return new FilterResults();
                }
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                List<DeviceName> filterList = (ArrayList<DeviceName>) results.values;
                if (results != null && results.count > 0) {
                    clear();
                    for (DeviceName people : filterList) {
                        add(people);
                        notifyDataSetChanged();
                    }
                }
            }
        };

        public FiltersAdapter(Context context, int resource, int textViewResourceId, List<DeviceName> items) {
            super(context, resource, textViewResourceId, items);
            this.context = context;
            this.items = items;
            tempItems = new ArrayList<DeviceName>(items);
            suggestions = new ArrayList<DeviceName>();
        }

        @NonNull
        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View view = convertView;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.row_first, parent, false);
            }
            DeviceName people = items.get(position);
            if (people != null) {
                TextView lblName = view.findViewById(R.id.first_tv);
                if (lblName != null) lblName.setText(people.getName());

                assert lblName != null;
                lblName.setOnClickListener(view1 -> {
                    if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(getContext())) {
                        if (!people.getName().isEmpty() && !people.getName().equals("null") && people.getType() != null && !String.valueOf(people.getType()).isEmpty() && prefManager.getUserType() != null) {
                            getDevices(people.getName(), String.valueOf(people.getType()));
                            binding.searchView.setVisibility(View.GONE);
                            binding.searchView.getText().clear();
                        } else {
                            Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "No Found Devices!", Snackbar.LENGTH_LONG);
                            snack.show();
                        }
                    } else {
                        final Dialog dialog = new Dialog(getContext());
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.no_internet_dialog);
                        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(getContext().getDrawable(R.drawable.pop_background));
                        LottieAnimationView lottieAnimationView = dialog.findViewById(R.id.animation_view);
                        Button RetryBtn = dialog.findViewById(R.id.btnDialog);
                        lottieAnimationView.playAnimation();
                        RetryBtn.setOnClickListener(view2 -> {
                            if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(getContext())) {
                                dialog.dismiss();
                            }
                        });
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setCancelable(false);
                        dialog.show();
                    }
                });

            }
            return view;
        }

        @NonNull
        @Override
        public Filter getFilter() {
            return nameFilter;
        }
    }

    public class MyKmlStyler implements KmlFeature.Styler {

        private final int mColor;
        private final MapView mapView;

        public MyKmlStyler(int mColor, MapView mapView) {
            this.mColor = mColor;
            this.mapView = mapView;
        }

        @Override
        public void onLineString(Polyline polyline, KmlPlacemark kmlPlacemark, KmlLineString kmlLineString) {
            try {
                CaSectionId.put(kmlPlacemark.getExtendedData("DeviceNumber"), polyline);
                CaPolylineList.add(polyline);
                polyline.getPaint().setStrokeJoin(Paint.Join.ROUND);
                polyline.setColor(Color.RED);
                polyline.setWidth(3.5f);
                polyline.setEnabled(true);
                polyline.setVisible(true);
                polyline.setGeodesic(true);
                polyline.setDensityMultiplier(7.5f);

                polyline.getOutlinePaint().setPathEffect(new DashPathEffect(new float[]{10, 20}, Path.Direction.CW.ordinal()));

                polyline.setOnClickListener((polyline1, mapView1, eventPos) -> {
                    networkId = kmlPlacemark.getExtendedData("NetworkId");
                    nodeId = kmlPlacemark.getExtendedData("ToNodeId");

                    DelSectionId = kmlPlacemark.getExtendedData("SectionId");
                    DelDeviceNumber = kmlPlacemark.getExtendedData("DeviceNumber");
                    DelNetworkId = kmlPlacemark.getExtendedData("NetworkId");
                    DelDeviceType = "1";

                    if (Config.isLoadFlow) {
                        highlightLoadFlowSection(polyline, kmlPlacemark.getExtendedData("SectionId"), 0);
                        LoadFlowBox loadFlowBox = new LoadFlowBox(mapView.getContext(), kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("DeviceType"), (ArrayList<String>) loadFlowList, kmlPlacemark.getExtendedData("CableId"));
                        loadFragment(loadFlowBox, "loadFlowBoxTag");
                        if (isTracing) {
                            reSetColor();
                        }
                    } else if (Config.isShortCircuit) {
                        highlightShortCircuitSection(polyline, kmlPlacemark.getExtendedData("SectionId"), 0);
                        ShortCircuitBox shortCircuitBox = new ShortCircuitBox(mapView.getContext(), (ArrayList<String>) shortCircuitList, kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("DeviceType"));
                        loadFragment(shortCircuitBox, "shortCircuitBoxTag");
                        if (isTracing) {
                            reSetColor();
                        }
                    } else {
                        highlightSection(polyline, "1", 0);
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("DeviceNumber", kmlPlacemark.getExtendedData("DeviceNumber"));
                        jsonObject.addProperty("DeviceType", kmlPlacemark.getExtendedData("DeviceType"));
                        CableSnippet cableSnipet = new CableSnippet(MapActivity.this, kmlPlacemark.getExtendedData("NetworkId"), kmlPlacemark.getExtendedData("SectionId"), kmlPlacemark.getExtendedData("CableId"), kmlPlacemark.getExtendedData("Length"), kmlPlacemark.getExtendedData("Voltage"), jsonObject);
                        cableSnipet.show();
                        if (isTracing) {
                            reSetColor();
                        }
                    }
                    mapView.invalidate();
                    return true;
                });

            } catch (Exception e) {
                Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
            }
        }

        @Override
        public void onPolygon(Polygon polygon, KmlPlacemark kmlPlacemark, KmlPolygon kmlPolygon) {

        }

        @Override
        public void onTrack(Polyline polyline, KmlPlacemark kmlPlacemark, KmlTrack kmlTrack) {

        }

        @Override
        public void onPoint(Marker marker, KmlPlacemark kmlPlacemark, KmlPoint kmlPoint) {

        }

        @Override
        public void onFeature(Overlay overlay, KmlFeature kmlFeature) {

        }
    }

    public class OverHeadKmlStyler implements KmlFeature.Styler {

        private final int mColor;
        private final MapView mapView;

        public OverHeadKmlStyler(int mColor, MapView mapView) {
            this.mColor = mColor;
            this.mapView = mapView;
        }

        @Override
        public void onLineString(Polyline polyline, KmlPlacemark kmlPlacemark, KmlLineString kmlLineString) {
            try {
                OhSectionId.put(kmlPlacemark.getExtendedData("DeviceNumber"), polyline);
                ohPolylineList.add(polyline);
                polyline.getPaint().setStrokeJoin(Paint.Join.ROUND);
                polyline.setColor(Color.BLUE);
                polyline.setWidth(3.5f);
                polyline.setGeodesic(true);
                polyline.setDensityMultiplier(7.5f);

                polyline.setOnClickListener((polyline1, mapView, eventPos) -> {
                    networkId = kmlPlacemark.getExtendedData("NetworkId");
                    nodeId = kmlPlacemark.getExtendedData("ToNodeId");

                    DelSectionId = kmlPlacemark.getExtendedData("SectionId");
                    DelDeviceNumber = kmlPlacemark.getExtendedData("DeviceNumber");
                    DelNetworkId = kmlPlacemark.getExtendedData("NetworkId");
                    DelDeviceType = "2";

                    if (Config.isLoadFlow) {
                        highlightLoadFlowSection(polyline1, kmlPlacemark.getExtendedData("SectionId"), 0);
                        LoadFlowBox loadFlowBox = new LoadFlowBox(mapView.getContext(), kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("DeviceType"), (ArrayList<String>) loadFlowList, kmlPlacemark.getExtendedData("LineId"));
                        loadFragment(loadFlowBox, "loadFlowBoxTag");
                        if (isTracing) {
                            reSetColor();
                        }
                    } else if (Config.isShortCircuit) {
                        highlightShortCircuitSection(polyline1, kmlPlacemark.getExtendedData("SectionId"), 0);
                        ShortCircuitBox shortCircuitBox = new ShortCircuitBox(mapView.getContext(), (ArrayList<String>) shortCircuitList, kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("DeviceType"));
                        loadFragment(shortCircuitBox, "shortCircuitBoxTag");
                        if (isTracing) {
                            reSetColor();
                        }
                    } else {
                        highlightSection(polyline1, "2", 0);
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("DeviceNumber", kmlPlacemark.getExtendedData("DeviceNumber"));
                        jsonObject.addProperty("DeviceType", kmlPlacemark.getExtendedData("DeviceType"));
                        OverheadSnippet overheadSnippet = new OverheadSnippet(MapActivity.this, kmlPlacemark.getExtendedData("NetworkId"), kmlPlacemark.getExtendedData("SectionId"), kmlPlacemark.getExtendedData("LineId"), kmlPlacemark.getExtendedData("Length"), kmlPlacemark.getExtendedData("Voltage"), jsonObject);
                        overheadSnippet.show();
                        if (isTracing) {
                            reSetColor();
                        }
                    }
                    mapView.invalidate();
                    return true;
                });
            } catch (Exception e) {
                Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
            }
        }

        @Override
        public void onFeature(Overlay overlay, KmlFeature kmlFeature) {

        }

        @Override
        public void onPoint(Marker marker, KmlPlacemark kmlPlacemark, KmlPoint kmlPoint) {

        }

        @Override
        public void onPolygon(Polygon polygon, KmlPlacemark kmlPlacemark, KmlPolygon kmlPolygon) {

        }

        @Override
        public void onTrack(Polyline polyline, KmlPlacemark kmlPlacemark, KmlTrack kmlTrack) {

        }
    }

    public class UnbalanceKmlStyler implements KmlFeature.Styler {

        private final int mColor;
        private final MapView mapView;

        public UnbalanceKmlStyler(int mColor, MapView mapView) {
            this.mColor = mColor;
            this.mapView = mapView;
        }

        @Override
        public void onLineString(Polyline polyline, KmlPlacemark kmlPlacemark, KmlLineString kmlLineString) {
            try {
                UnBalSectionId.put(kmlPlacemark.getExtendedData("DeviceNumber"), polyline);
                unBalPolylineList.add(polyline);
                Paint p = new Paint();
                p.setAlpha(12);
                p.setStrokeCap(Paint.Cap.SQUARE);
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeCap(Paint.Cap.BUTT);
                polyline.getPaint().set(p);
                polyline.setColor(Color.DKGRAY);
                polyline.setWidth(3.5f);
                polyline.setDensityMultiplier(7.5f);

                polyline.setOnClickListener((polyline1, mapView, eventPos) -> {
                    networkId = kmlPlacemark.getExtendedData("NetworkId");
                    nodeId = kmlPlacemark.getExtendedData("ToNodeId");

                    DelSectionId = kmlPlacemark.getExtendedData("SectionId");
                    DelDeviceNumber = kmlPlacemark.getExtendedData("DeviceNumber");
                    DelNetworkId = kmlPlacemark.getExtendedData("NetworkId");
                    DelDeviceType = "23";

                    if (Config.isLoadFlow) {
                        highlightLoadFlowSection(polyline1, kmlPlacemark.getExtendedData("SectionId"), 0);
                        LoadFlowBox loadFlowBox = new LoadFlowBox(mapView.getContext(), kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("DeviceType"), (ArrayList<String>) loadFlowList, "");
                        loadFragment(loadFlowBox, "loadFlowBoxTag");
                        if (isTracing) {
                            reSetColor();
                        }
                    } else if (Config.isShortCircuit) {
                        highlightShortCircuitSection(polyline1, kmlPlacemark.getExtendedData("SectionId"), 0);
                        ShortCircuitBox shortCircuitBox = new ShortCircuitBox(mapView.getContext(), (ArrayList<String>) shortCircuitList, kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("DeviceType"));
                        loadFragment(shortCircuitBox, "shortCircuitBoxTag");
                        if (isTracing) {
                            reSetColor();
                        }
                    } else {
                        highlightSection(polyline1, "23", 0);
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("DeviceNumber", kmlPlacemark.getExtendedData("DeviceNumber"));
                        jsonObject.addProperty("DeviceType", kmlPlacemark.getExtendedData("DeviceType"));
                        UnbalanceSnippet unbalanceSnippet = new UnbalanceSnippet(MapActivity.this, kmlPlacemark.getExtendedData("NetworkId"), kmlPlacemark.getExtendedData("SectionId"), kmlPlacemark.getExtendedData("LineId"), kmlPlacemark.getExtendedData("Length"), jsonObject);
                        unbalanceSnippet.show();
                        if (isTracing) {
                            reSetColor();
                        }
                    }
                    mapView.invalidate();
                    return true;

                });
            } catch (Exception e) {
                Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
            }
        }

        @Override
        public void onFeature(Overlay overlay, KmlFeature kmlFeature) {

        }

        @Override
        public void onPoint(Marker marker, KmlPlacemark kmlPlacemark, KmlPoint kmlPoint) {

        }

        @Override
        public void onPolygon(Polygon polygon, KmlPlacemark kmlPlacemark, KmlPolygon kmlPolygon) {

        }

        @Override
        public void onTrack(Polyline polyline, KmlPlacemark kmlPlacemark, KmlTrack kmlTrack) {

        }
    }

    public class SectionNodeKmlStyler implements KmlFeature.Styler {

        private final int mColor;
        private final MapView mapView;

        public SectionNodeKmlStyler(int mColor, MapView mapView) {
            this.mColor = mColor;
            this.mapView = mapView;
        }

        @Override
        public void onLineString(Polyline polyline, KmlPlacemark kmlPlacemark, KmlLineString kmlLineString) {
            polyline.setColor(Color.BLACK);
            polyline.setWidth(3.5f);
            polyline.setEnabled(true);
            polyline.setVisible(true);
            polyline.setGeodesic(true);
            polyline.setDensityMultiplier(9.5f);

            secNodeList.add(polyline);
            secNodeSectionId.put(kmlPlacemark.getExtendedData("SectionId"), polyline);

            polyline.setOnClickListener((polyline1, mapView, eventPos) -> {
                try {
                    if (Config.isLoadFlow) {
                        if (kmlPlacemark.getExtendedData("DeviceType").equals("8")) {
                            //Breaker
                            if (breakerSectionId.get(kmlPlacemark.getExtendedData("DeviceNumber")) != null) {
                                highlightLoadFlowDevice(breakerSectionId.get(kmlPlacemark.getExtendedData("DeviceNumber")), "8", kmlPlacemark.getExtendedData("DeviceNumber"), 0);
                                LoadFlowBox loadFlowBox = new LoadFlowBox(mapView.getContext(), kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("DeviceType"), (ArrayList<String>) loadFlowList, kmlPlacemark.getExtendedData("EquipmentId"));
                                loadFragment(loadFlowBox, "loadFlowBoxTag");
                                if (isTracing) {
                                    reSetColor();
                                }
                                mapView.invalidate();
                            }
                        } else if (kmlPlacemark.getExtendedData("DeviceType").equals("5")) {
                            //Transformer
                            if (transformerSectionId.get(kmlPlacemark.getExtendedData("DeviceNumber")) != null) {
                                highlightLoadFlowDevice(transformerSectionId.get(kmlPlacemark.getExtendedData("DeviceNumber")), "5", kmlPlacemark.getExtendedData("DeviceNumber"), 0);
                                LoadFlowBox loadFlowBox = new LoadFlowBox(mapView.getContext(), kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("DeviceType"), (ArrayList<String>) loadFlowList, kmlPlacemark.getExtendedData("EquipmentId"));
                                loadFragment(loadFlowBox, "loadFlowBoxTag");
                                if (isTracing) {
                                    reSetColor();
                                }
                                mapView.invalidate();
                            }
                        } else if (kmlPlacemark.getExtendedData("DeviceType").equals("13")) {
                            //switch
                            if (switchSectionId.get(kmlPlacemark.getExtendedData("DeviceNumber")) != null) {
                                highlightLoadFlowDevice(switchSectionId.get(kmlPlacemark.getExtendedData("DeviceNumber")), "13", kmlPlacemark.getExtendedData("DeviceNumber"), 0);
                                LoadFlowBox loadFlowBox = new LoadFlowBox(mapView.getContext(), kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("DeviceType"), (ArrayList<String>) loadFlowList, kmlPlacemark.getExtendedData("EquipmentId"));
                                loadFragment(loadFlowBox, "loadFlowBoxTag");
                                if (isTracing) {
                                    reSetColor();
                                }
                                mapView.invalidate();
                            }
                        } else if (kmlPlacemark.getExtendedData("DeviceType").equals("14")) {
                            //Fuse
                            if (fuseSectionId.get(kmlPlacemark.getExtendedData("DeviceNumber")) != null) {
                                highlightLoadFlowDevice(fuseSectionId.get(kmlPlacemark.getExtendedData("DeviceNumber")), "14", kmlPlacemark.getExtendedData("DeviceNumber"), 0);
                                LoadFlowBox loadFlowBox = new LoadFlowBox(mapView.getContext(), kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("DeviceType"), (ArrayList<String>) loadFlowList, kmlPlacemark.getExtendedData("EquipmentId"));
                                loadFragment(loadFlowBox, "loadFlowBoxTag");
                                if (isTracing) {
                                    reSetColor();
                                }
                                mapView.invalidate();
                            }
                        } else if (kmlPlacemark.getExtendedData("DeviceType").equals("17")) {
                            //Shunt capacitor
                            if (capacitorSectionId.get(kmlPlacemark.getExtendedData("DeviceNumber")) != null) {
                                highlightLoadFlowDevice(capacitorSectionId.get(kmlPlacemark.getExtendedData("DeviceNumber")), "17", kmlPlacemark.getExtendedData("DeviceNumber"), 0);
                                LoadFlowBox loadFlowBox = new LoadFlowBox(mapView.getContext(), kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("DeviceType"), (ArrayList<String>) loadFlowList, kmlPlacemark.getExtendedData("EquipmentId"));
                                loadFragment(loadFlowBox, "loadFlowBoxTag");
                                if (isTracing) {
                                    reSetColor();
                                }
                                mapView.invalidate();
                            }
                        } else if (kmlPlacemark.getExtendedData("DeviceType").equals("20")) {
                            //Spoatload
                            if (spotLoadSectionId.get(kmlPlacemark.getExtendedData("DeviceNumber")) != null) {
                                highlightLoadFlowDevice(spotLoadSectionId.get(kmlPlacemark.getExtendedData("DeviceNumber")), "20", kmlPlacemark.getExtendedData("DeviceNumber"), 0);
                                LoadFlowBox loadFlowBox = new LoadFlowBox(mapView.getContext(), kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("DeviceType"), (ArrayList<String>) loadFlowList, "");
                                loadFragment(loadFlowBox, "loadFlowBoxTag");
                                if (isTracing) {
                                    reSetColor();
                                }
                                mapView.invalidate();
                            }
                        }
                    } else if (Config.isShortCircuit) {
                        if (kmlPlacemark.getExtendedData("DeviceType").equals("8")) {
                            //Breaker
                            if (breakerSectionId.get(kmlPlacemark.getExtendedData("DeviceNumber")) != null) {
                                highlightShortCircuitDevice(breakerSectionId.get(kmlPlacemark.getExtendedData("DeviceNumber")), "8", kmlPlacemark.getExtendedData("DeviceNumber"), 0);
                                ShortCircuitBox shortCircuitBox = new ShortCircuitBox(mapView.getContext(), (ArrayList<String>) shortCircuitList, kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("DeviceType"));
                                loadFragment(shortCircuitBox, "shortCircuitBoxTag");
                                if (isTracing) {
                                    reSetColor();
                                }
                                mapView.invalidate();
                            }
                        } else if (kmlPlacemark.getExtendedData("DeviceType").equals("5")) {
                            //Transformer
                            if (transformerSectionId.get(kmlPlacemark.getExtendedData("DeviceNumber")) != null) {
                                highlightShortCircuitDevice(transformerSectionId.get(kmlPlacemark.getExtendedData("DeviceNumber")), "5", kmlPlacemark.getExtendedData("DeviceNumber"), 0);
                                ShortCircuitBox shortCircuitBox = new ShortCircuitBox(mapView.getContext(), (ArrayList<String>) shortCircuitList, kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("DeviceType"));
                                loadFragment(shortCircuitBox, "shortCircuitBoxTag");
                                if (isTracing) {
                                    reSetColor();
                                }
                                mapView.invalidate();
                            }

                        } else if (kmlPlacemark.getExtendedData("DeviceType").equals("13")) {
                            //switch
                            if (switchSectionId.get(kmlPlacemark.getExtendedData("DeviceNumber")) != null) {
                                highlightShortCircuitDevice(switchSectionId.get(kmlPlacemark.getExtendedData("DeviceNumber")), "13", kmlPlacemark.getExtendedData("DeviceNumber"), 0);
                                ShortCircuitBox shortCircuitBox = new ShortCircuitBox(mapView.getContext(), (ArrayList<String>) shortCircuitList, kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("DeviceType"));
                                loadFragment(shortCircuitBox, "shortCircuitBoxTag");
                                if (isTracing) {
                                    reSetColor();
                                }
                                mapView.invalidate();
                            }
                        } else if (kmlPlacemark.getExtendedData("DeviceType").equals("14")) {
                            //Fuse
                            if (fuseSectionId.get(kmlPlacemark.getExtendedData("DeviceNumber")) != null) {
                                highlightShortCircuitDevice(fuseSectionId.get(kmlPlacemark.getExtendedData("DeviceNumber")), "14", kmlPlacemark.getExtendedData("DeviceNumber"), 0);
                                ShortCircuitBox shortCircuitBox = new ShortCircuitBox(mapView.getContext(), (ArrayList<String>) shortCircuitList, kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("DeviceType"));
                                loadFragment(shortCircuitBox, "shortCircuitBoxTag");
                                if (isTracing) {
                                    reSetColor();
                                }
                                mapView.invalidate();
                            }
                        } else if (kmlPlacemark.getExtendedData("DeviceType").equals("17")) {
                            //Shunt capacitor
                            if (capacitorSectionId.get(kmlPlacemark.getExtendedData("DeviceNumber")) != null) {
                                highlightShortCircuitDevice(capacitorSectionId.get(kmlPlacemark.getExtendedData("DeviceNumber")), "17", kmlPlacemark.getExtendedData("DeviceNumber"), 0);
                                ShortCircuitBox shortCircuitBox = new ShortCircuitBox(mapView.getContext(), (ArrayList<String>) shortCircuitList, kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("DeviceType"));
                                loadFragment(shortCircuitBox, "shortCircuitBoxTag");
                                if (isTracing) {
                                    reSetColor();
                                }
                                mapView.invalidate();
                            }
                        } else if (kmlPlacemark.getExtendedData("DeviceType").equals("20")) {
                            //Spoatload
                            if (spotLoadSectionId.get(kmlPlacemark.getExtendedData("DeviceNumber")) != null) {
                                highlightShortCircuitDevice(spotLoadSectionId.get(kmlPlacemark.getExtendedData("DeviceNumber")), "20", kmlPlacemark.getExtendedData("DeviceNumber"), 0);
                                ShortCircuitBox shortCircuitBox = new ShortCircuitBox(mapView.getContext(), (ArrayList<String>) shortCircuitList, kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("DeviceType"));
                                loadFragment(shortCircuitBox, "shortCircuitBoxTag");
                                if (isTracing) {
                                    reSetColor();
                                }
                                mapView.invalidate();
                            }
                        }
                    } else {
                        if (kmlPlacemark.getExtendedData("DeviceType").equals("8")) {
                            //Breaker
                            networkId = kmlPlacemark.getExtendedData("NetworkId");
                            nodeId = kmlPlacemark.getExtendedData("ToNodeId");

                            DelSectionId = kmlPlacemark.getExtendedData("SectionId");
                            DelDeviceNumber = kmlPlacemark.getExtendedData("DeviceNumber");
                            DelNetworkId = kmlPlacemark.getExtendedData("NetworkId");
                            DelDeviceType = kmlPlacemark.getExtendedData("DeviceType");

                            if (breakerSectionId.get(kmlPlacemark.getExtendedData("DeviceNumber")) != null) {
                                highlightDevice(breakerSectionId.get(kmlPlacemark.getExtendedData("DeviceNumber")), "8", kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("SectionId"), 0);
                                JsonObject jsonObject = new JsonObject();
                                jsonObject.addProperty("DeviceNumber", kmlPlacemark.getExtendedData("DeviceNumber"));
                                jsonObject.addProperty("DeviceType", kmlPlacemark.getExtendedData("DeviceType"));
                                BreakerSnippet breakerSnippet = new BreakerSnippet(MapActivity.this, kmlPlacemark.getExtendedData("SectionId"), kmlPlacemark.getExtendedData("DeviceNumber"), "0", kmlPlacemark.getExtendedData("NetworkId"), kmlPlacemark.getExtendedData("DeviceType"), "0", jsonObject);
                                breakerSnippet.show();
                                if (isTracing) {
                                    reSetColor();
                                }
                                mapView.invalidate();
                            }
                            mapView.invalidate();
                        } else if (kmlPlacemark.getExtendedData("DeviceType").equals("5")) {
                            //Transformer
                            networkId = kmlPlacemark.getExtendedData("NetworkId");
                            nodeId = kmlPlacemark.getExtendedData("ToNodeId");

                            DelSectionId = kmlPlacemark.getExtendedData("SectionId");
                            DelDeviceNumber = kmlPlacemark.getExtendedData("DeviceNumber");
                            DelNetworkId = kmlPlacemark.getExtendedData("NetworkId");
                            DelDeviceType = kmlPlacemark.getExtendedData("DeviceType");

                            if (transformerSectionId.get(kmlPlacemark.getExtendedData("DeviceNumber")) != null) {
                                highlightDevice(transformerSectionId.get(kmlPlacemark.getExtendedData("DeviceNumber")), "5", kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("SectionId"), 0);
                                JsonObject jsonObject = new JsonObject();
                                jsonObject.addProperty("DeviceNumber", kmlPlacemark.getExtendedData("DeviceNumber"));
                                jsonObject.addProperty("DeviceType", kmlPlacemark.getExtendedData("DeviceType"));
                                TransformerSnippet transformerSnippet = new TransformerSnippet(MapActivity.this, kmlPlacemark.getExtendedData("SectionId"), kmlPlacemark.getExtendedData("DeviceNumber"), "0", kmlPlacemark.getExtendedData("NetworkId"), kmlPlacemark.getExtendedData("DeviceType"), "0", jsonObject);
                                transformerSnippet.show();
                                if (isTracing) {
                                    reSetColor();
                                }
                                mapView.invalidate();
                            }

                        } else if (kmlPlacemark.getExtendedData("DeviceType").equals("13")) {
                            //switch
                            networkId = kmlPlacemark.getExtendedData("NetworkId");
                            nodeId = kmlPlacemark.getExtendedData("ToNodeId");

                            DelSectionId = kmlPlacemark.getExtendedData("SectionId");
                            DelDeviceNumber = kmlPlacemark.getExtendedData("DeviceNumber");
                            DelNetworkId = kmlPlacemark.getExtendedData("NetworkId");
                            DelDeviceType = kmlPlacemark.getExtendedData("DeviceType");

                            if (switchSectionId.get(kmlPlacemark.getExtendedData("DeviceNumber")) != null) {
                                highlightDevice(switchSectionId.get(kmlPlacemark.getExtendedData("DeviceNumber")), "13", kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("SectionId"), 0);
                                JsonObject jsonObject = new JsonObject();
                                jsonObject.addProperty("DeviceNumber", kmlPlacemark.getExtendedData("DeviceNumber"));
                                jsonObject.addProperty("DeviceType", kmlPlacemark.getExtendedData("DeviceType"));
                                SwitchSnippet switchSnippet = new SwitchSnippet(MapActivity.this, kmlPlacemark.getExtendedData("SectionId"), kmlPlacemark.getExtendedData("DeviceNumber"), "", kmlPlacemark.getExtendedData("NetworkId"), kmlPlacemark.getExtendedData("DeviceType"), "0", jsonObject);
                                switchSnippet.show();
                                if (isTracing) {
                                    reSetColor();
                                }
                                mapView.invalidate();
                            }

                        } else if (kmlPlacemark.getExtendedData("DeviceType").equals("14")) {
                            //Fuse
                            networkId = kmlPlacemark.getExtendedData("NetworkId");
                            nodeId = kmlPlacemark.getExtendedData("ToNodeId");

                            DelSectionId = kmlPlacemark.getExtendedData("SectionId");
                            DelDeviceNumber = kmlPlacemark.getExtendedData("DeviceNumber");
                            DelNetworkId = kmlPlacemark.getExtendedData("NetworkId");
                            DelDeviceType = kmlPlacemark.getExtendedData("DeviceType");

                            if (fuseSectionId.get(kmlPlacemark.getExtendedData("DeviceNumber")) != null) {
                                highlightDevice(fuseSectionId.get(kmlPlacemark.getExtendedData("DeviceNumber")), "14", kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("SectionId"), 0);
                                JsonObject jsonObject = new JsonObject();
                                jsonObject.addProperty("DeviceNumber", kmlPlacemark.getExtendedData("DeviceNumber"));
                                jsonObject.addProperty("DeviceType", kmlPlacemark.getExtendedData("DeviceType"));
                                FuseSnippet fuseSnippet = new FuseSnippet(MapActivity.this, kmlPlacemark.getExtendedData("SectionId"), kmlPlacemark.getExtendedData("DeviceNumber"), "", kmlPlacemark.getExtendedData("NetworkId"), kmlPlacemark.getExtendedData("DeviceType"), "0", jsonObject);
                                fuseSnippet.show();
                                if (isTracing) {
                                    reSetColor();
                                }
                                mapView.invalidate();
                            }

                        } else if (kmlPlacemark.getExtendedData("DeviceType").equals("17")) {
                            //Shunt capacitor
                            networkId = kmlPlacemark.getExtendedData("NetworkId");
                            nodeId = kmlPlacemark.getExtendedData("ToNodeId");

                            DelSectionId = kmlPlacemark.getExtendedData("SectionId");
                            DelDeviceNumber = kmlPlacemark.getExtendedData("DeviceNumber");
                            DelNetworkId = kmlPlacemark.getExtendedData("NetworkId");
                            DelDeviceType = kmlPlacemark.getExtendedData("DeviceType");

                            if (capacitorSectionId.get(kmlPlacemark.getExtendedData("DeviceNumber")) != null) {
                                highlightDevice(capacitorSectionId.get(kmlPlacemark.getExtendedData("DeviceNumber")), "61", kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("SectionId"), 0);
                                JsonObject jsonObject = new JsonObject();
                                jsonObject.addProperty("DeviceNumber", kmlPlacemark.getExtendedData("DeviceNumber"));
                                jsonObject.addProperty("DeviceType", kmlPlacemark.getExtendedData("DeviceType"));
                                ShuntCapacitorSnippet shuntCapacitorSnippet = new ShuntCapacitorSnippet(MapActivity.this, kmlPlacemark.getExtendedData("SectionId"), kmlPlacemark.getExtendedData("DeviceNumber"), "", kmlPlacemark.getExtendedData("NetworkId"), kmlPlacemark.getExtendedData("DeviceType"), jsonObject);
                                shuntCapacitorSnippet.show();
                                if (isTracing) {
                                    reSetColor();
                                }
                                mapView.invalidate();
                            }

                        } else if (kmlPlacemark.getExtendedData("DeviceType").equals("20")) {
                            //Spoatload
                            networkId = kmlPlacemark.getExtendedData("NetworkId");
                            nodeId = kmlPlacemark.getExtendedData("ToNodeId");

                            DelSectionId = kmlPlacemark.getExtendedData("SectionId");
                            DelDeviceNumber = kmlPlacemark.getExtendedData("DeviceNumber");
                            DelNetworkId = kmlPlacemark.getExtendedData("NetworkId");
                            DelDeviceType = kmlPlacemark.getExtendedData("DeviceType");

                            if (spotLoadSectionId.get(kmlPlacemark.getExtendedData("DeviceNumber")) != null) {
                                highlightDevice(spotLoadSectionId.get(kmlPlacemark.getExtendedData("DeviceNumber")), "20", kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("SectionId"), 0);
                                JsonObject jsonObject = new JsonObject();
                                jsonObject.addProperty("DeviceNumber", kmlPlacemark.getExtendedData("DeviceNumber"));
                                jsonObject.addProperty("DeviceType", kmlPlacemark.getExtendedData("DeviceType"));
                                SpotLoadSnippet spotLoadSnippet = new SpotLoadSnippet(MapActivity.this, kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("NetworkId"), "", kmlPlacemark.getExtendedData("Customers"), kmlPlacemark.getExtendedData("DeviceType"), jsonObject);
                                spotLoadSnippet.show();
                                if (isTracing) {
                                    reSetColor();
                                }
                                mapView.invalidate();
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.d("Exception", e.getLocalizedMessage());
                }
                return true;
            });
        }

        @Override
        public void onPoint(Marker marker, KmlPlacemark kmlPlacemark, KmlPoint kmlPoint) {

        }

        @Override
        public void onFeature(Overlay overlay, KmlFeature kmlFeature) {

        }

        @Override
        public void onPolygon(Polygon polygon, KmlPlacemark kmlPlacemark, KmlPolygon kmlPolygon) {

        }

        @Override
        public void onTrack(Polyline polyline, KmlPlacemark kmlPlacemark, KmlTrack kmlTrack) {

        }
    }

    public class circuitBreakerKmlStyler implements KmlFeature.Styler {

        private final int mColor;
        private final MapView mapView;

        public circuitBreakerKmlStyler(int mColor, MapView mapView) {
            this.mColor = mColor;
            this.mapView = mapView;
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public void onPoint(Marker marker, KmlPlacemark kmlPlacemark, KmlPoint kmlPoint) {
            try {
                int i = kmlPlacemark.mGeometry.mCoordinates.size() - 1;
                GeoPoint geoPoint = new GeoPoint(kmlPoint.mCoordinates.get(i).getLatitude(), kmlPoint.mCoordinates.get(i).getLongitude());
                marker.setPosition(geoPoint);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);

                breakerSectionId.put(kmlPlacemark.getExtendedData("DeviceNumber"), marker);
                breakerList.add(marker);

                if (kmlPlacemark.getExtendedData("ClosedPhase").equalsIgnoreCase("7")) {
                    Drawable drawable = getResources().getDrawable(R.drawable.breaker);
                    Bitmap bites = drawableToBitmap(drawable);
                    Bitmap customizedBitmap = changeBitmapColor(bites, Color.BLACK);
                    marker.setRotation((float) ResponseDataUtils.CalculateAng(Double.parseDouble(kmlPlacemark.getExtendedData("FromNode_Y_l")), Double.parseDouble(kmlPlacemark.getExtendedData("ToNode_Y_l")), Double.parseDouble(kmlPlacemark.getExtendedData("FromNode_X_l")), Double.parseDouble(kmlPlacemark.getExtendedData("ToNode_X_l"))));
                    marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(customizedBitmap, 0, 135)));
                } else {
                    Drawable drawable = getResources().getDrawable(R.drawable.openbreaker);
                    Bitmap bites = drawableToBitmap(drawable);
                    Bitmap customizedBitmap = changeBitmapColor(bites, Color.BLACK);
                    marker.setIcon(new BitmapDrawable(mapView.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(customizedBitmap, 0, 0)));
                    marker.setRotation((float) ResponseDataUtils.CalculateAng(Double.parseDouble(kmlPlacemark.getExtendedData("FromNode_X_l")), Double.parseDouble(kmlPlacemark.getExtendedData("ToNode_X_l")), Double.parseDouble(kmlPlacemark.getExtendedData("FromNode_Y_l")), Double.parseDouble(kmlPlacemark.getExtendedData("ToNode_Y_l"))));
                }

                marker.setOnMarkerClickListener((marker1, mapView) -> {
                    networkId = kmlPlacemark.getExtendedData("NetworkId");
                    nodeId = kmlPlacemark.getExtendedData("ToNodeId");

                    DelSectionId = kmlPlacemark.getExtendedData("SectionId");
                    DelDeviceNumber = kmlPlacemark.getExtendedData("DeviceNumber");
                    DelNetworkId = kmlPlacemark.getExtendedData("NetworkId");
                    DelDeviceType = "8";

                    if (Config.isLoadFlow) {
                        highlightLoadFlowDevice(marker1, "8", kmlPlacemark.getExtendedData("DeviceNumber"), Integer.parseInt(kmlPlacemark.getExtendedData("Location")));
                        LoadFlowBox loadFlowBox = new LoadFlowBox(mapView.getContext(), kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("DeviceType"), (ArrayList<String>) loadFlowList, kmlPlacemark.getExtendedData("EquipmentId"));
                        loadFragment(loadFlowBox, "loadFlowBoxTag");
                        if (isTracing) {
                            reSetColor();
                        }
                    } else if (Config.isShortCircuit) {
                        highlightShortCircuitDevice(marker1, "8", kmlPlacemark.getExtendedData("DeviceNumber"), Integer.parseInt(kmlPlacemark.getExtendedData("Location")));
                        ShortCircuitBox shortCircuitBox = new ShortCircuitBox(mapView.getContext(), (ArrayList<String>) shortCircuitList, kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("DeviceType"));
                        loadFragment(shortCircuitBox, "shortCircuitBoxTag");
                        if (isTracing) {
                            reSetColor();
                        }
                    } else {
                        highlightDevice(marker1, "8", kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("SectionId"), Integer.parseInt(kmlPlacemark.getExtendedData("Location")));
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("DeviceNumber", kmlPlacemark.getExtendedData("DeviceNumber"));
                        jsonObject.addProperty("DeviceType", kmlPlacemark.getExtendedData("DeviceType"));
                        BreakerSnippet breakerSnippet = new BreakerSnippet(MapActivity.this, kmlPlacemark.getExtendedData("SectionId"), kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("EquipmentId"), kmlPlacemark.getExtendedData("NetworkId"), kmlPlacemark.getExtendedData("DeviceType"), kmlPlacemark.getExtendedData("Voltage"), jsonObject);
                        breakerSnippet.show();
                        if (isTracing) {
                            reSetColor();
                        }
                    }
                    mapView.invalidate();
                    return true;
                });

                if (caSectionList.contains(kmlPlacemark.getExtendedData("SectionId"))) {
                    int CaSize = CaObject.getJSONArray("features").length();
                    for (int j = 0; j < CaSize; j++) {
                        if (CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("properties").getString("SectionId").equals(kmlPlacemark.getExtendedData("SectionId")) && CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").length() > 2 && kmlPlacemark.getExtendedData("Location").equals("2")) {
                            String fromY = String.valueOf(CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").length() - 2).get(0));
                            String fromX = String.valueOf(CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").length() - 2).get(1));
//                            marker.setRotation((float) ResponseDataUtils.CalculateAng(Double.parseDouble(fromX), Double.parseDouble(kmlPlacemark.getExtendedData("ToNode_X_l")), Double.parseDouble(fromY), Double.parseDouble(kmlPlacemark.getExtendedData("ToNode_Y_l"))));
                            marker.setRotation((float) ResponseDataUtils.CalculateAng(Double.parseDouble(fromX), Double.parseDouble(kmlPlacemark.getExtendedData("ToNode_X_l")), Double.parseDouble(fromY), Double.parseDouble(kmlPlacemark.getExtendedData("ToNode_Y_l"))));
                            Drawable drawable = getResources().getDrawable(R.drawable.breaker);
                            Bitmap bites = drawableToBitmap(drawable);
                            Bitmap customizedBitmap = changeBitmapColor(bites, Color.BLACK);
                            marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(customizedBitmap, 0, 90)));
                            Log.d("BreakerAngelmethod call ", kmlPlacemark.getExtendedData("SectionId") + "  " + "fromX" + fromX + "toX" + kmlPlacemark.getExtendedData("ToNode_X_l") + "fromY" + fromY + " " + "toY" + kmlPlacemark.getExtendedData("ToNode_Y_l"));
                        } else if (CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("properties").getString("SectionId").equals(kmlPlacemark.getExtendedData("SectionId")) && CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").length() > 2 && !kmlPlacemark.getExtendedData("Location").equals("2")) {
                            String toY = String.valueOf(CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(1).get(0));
                            String toX = String.valueOf(CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(1).get(1));
                            marker.setRotation((float) ResponseDataUtils.CalculateAng(Double.parseDouble(kmlPlacemark.getExtendedData("FromNode_X_l")), Double.parseDouble(toX), Double.parseDouble(kmlPlacemark.getExtendedData("FromNode_Y_l")), Double.parseDouble(toY)));
                        }
                    }
                } else if (ohSectionList.contains(kmlPlacemark.getExtendedData("SectionId"))) {
                    int OhSize = OhObject.getJSONArray("features").length();
                    for (int k = 0; k < OhSize; k++) {
                        if (OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("properties").getString("SectionId").equals(kmlPlacemark.getExtendedData("SectionId")) && OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").length() > 2 && kmlPlacemark.getExtendedData("Location").equals("2")) {
                            String fromY = String.valueOf(OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").length() - 2).get(0));
                            String fromX = String.valueOf(OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").length() - 2).get(1));
                            marker.setRotation((float) ResponseDataUtils.CalculateAng(Double.parseDouble(fromX), Double.parseDouble(kmlPlacemark.getExtendedData("ToNode_X_l")), Double.parseDouble(fromY), Double.parseDouble(kmlPlacemark.getExtendedData("ToNode_Y_l"))));
                        } else if (OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("properties").getString("SectionId").equals(kmlPlacemark.getExtendedData("SectionId")) && OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").length() > 2 && !kmlPlacemark.getExtendedData("Location").equals("2")) {
                            String toX = String.valueOf(OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(1).get(1));
                            String toY = String.valueOf(OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(1).get(0));
                            marker.setRotation((float) ResponseDataUtils.CalculateAng(Double.parseDouble(kmlPlacemark.getExtendedData("FromNode_X_l")), Double.parseDouble(toX), Double.parseDouble(kmlPlacemark.getExtendedData("FromNode_Y_l")), Double.parseDouble(toY)));
                        }
                    }
                }
            } catch (Exception e) {
                Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
            }
        }

        @Override
        public void onFeature(Overlay overlay, KmlFeature kmlFeature) {

        }

        @Override
        public void onLineString(Polyline polyline, KmlPlacemark kmlPlacemark, KmlLineString kmlLineString) {

        }

        @Override
        public void onPolygon(Polygon polygon, KmlPlacemark kmlPlacemark, KmlPolygon kmlPolygon) {

        }

        @Override
        public void onTrack(Polyline polyline, KmlPlacemark kmlPlacemark, KmlTrack kmlTrack) {

        }
    }

    public class DistributionTransferKmlStyler implements KmlFeature.Styler {

        private final int mColor;
        private final MapView mapView;

        public DistributionTransferKmlStyler(int mColor, MapView mapView) {
            this.mColor = mColor;
            this.mapView = mapView;
        }

        @Override
        public void onPoint(Marker marker, KmlPlacemark kmlPlacemark, KmlPoint kmlPoint) {
            try {
                String fromX = kmlPlacemark.getExtendedData("FromNode_X_l");
                String fromY = kmlPlacemark.getExtendedData("FromNode_Y_l");
                String toX = kmlPlacemark.getExtendedData("ToNode_X_l");
                String toY = kmlPlacemark.getExtendedData("ToNode_Y_l");
                int i = kmlPlacemark.mGeometry.mCoordinates.size() - 1;
                GeoPoint geoPoint = new GeoPoint(kmlPoint.mCoordinates.get(i).getLatitude(), kmlPoint.mCoordinates.get(i).getLongitude());
                marker.setPosition(geoPoint);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                transformerSectionId.put(kmlPlacemark.getExtendedData("DeviceNumber"), marker);
                transformerList.add(marker);

                Bitmap customizedBitmap = null;
                if (kmlPlacemark.getExtendedData("Status").equalsIgnoreCase("0")) {
                    @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.transformer);
                    Bitmap bites = drawableToBitmap(drawable);
                    customizedBitmap = changeBitmapColor(bites, Color.BLACK);

                    marker.setRotation((float) ResponseDataUtils.CalculateAng(Double.parseDouble(fromY), Double.parseDouble(toY), Double.parseDouble(fromX), Double.parseDouble(toX)));
                    if (kmlPlacemark.getExtendedData("Location").equals("2")) {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(customizedBitmap, 135, 0)));
                    } else if (kmlPlacemark.getExtendedData("Location").equals("1")) {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(customizedBitmap, 0, 135)));
                    } else {
                        marker.setIcon(new BitmapDrawable(binding.map.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(customizedBitmap, 0, 0)));
                    }
                } else {
                    @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.opentransformer);
                    Bitmap bites = drawableToBitmap(drawable);
                    customizedBitmap = changeBitmapColor(bites, Color.BLACK);
                    marker.setIcon(new BitmapDrawable(mapView.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(customizedBitmap, 0, 0)));
                    marker.setRotation((float) ResponseDataUtils.CalculateAng(Double.parseDouble(fromX), Double.parseDouble(toX), Double.parseDouble(fromY), Double.parseDouble(toY)));
                }

                marker.setOnMarkerClickListener((marker1, mapView) -> {
                    networkId = kmlPlacemark.getExtendedData("NetworkId");
                    nodeId = kmlPlacemark.getExtendedData("ToNodeId");

                    DelSectionId = kmlPlacemark.getExtendedData("SectionId");
                    DelDeviceNumber = kmlPlacemark.getExtendedData("DeviceNumber");
                    DelNetworkId = kmlPlacemark.getExtendedData("NetworkId");
                    DelDeviceType = "5";

                    if (Config.isLoadFlow) {
                        highlightLoadFlowDevice(marker1, "5", kmlPlacemark.getExtendedData("DeviceNumber"), Integer.parseInt(kmlPlacemark.getExtendedData("Location")));
                        LoadFlowBox loadFlowBox = new LoadFlowBox(mapView.getContext(), kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("DeviceType"), (ArrayList<String>) loadFlowList, kmlPlacemark.getExtendedData("EquipmentId"));
                        loadFragment(loadFlowBox, "loadFlowBoxTag");
                        if (isTracing) {
                            reSetColor();
                        }
                    } else if (Config.isShortCircuit) {
                        highlightShortCircuitDevice(marker1, "5", kmlPlacemark.getExtendedData("DeviceNumber"), Integer.parseInt(kmlPlacemark.getExtendedData("Location")));
                        ShortCircuitBox shortCircuitBox = new ShortCircuitBox(mapView.getContext(), (ArrayList<String>) shortCircuitList, kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("DeviceType"));
                        loadFragment(shortCircuitBox, "shortCircuitBoxTag");
                        if (isTracing) {
                            reSetColor();
                        }
                    } else {
                        highlightDevice(marker1, "5", kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("SectionId"), Integer.parseInt(kmlPlacemark.getExtendedData("Location")));
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("DeviceNumber", kmlPlacemark.getExtendedData("DeviceNumber"));
                        jsonObject.addProperty("DeviceType", kmlPlacemark.getExtendedData("DeviceType"));
                        TransformerSnippet transformerSnippet = new TransformerSnippet(MapActivity.this, kmlPlacemark.getExtendedData("SectionId"), kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("EquipmentId"), kmlPlacemark.getExtendedData("NetworkId"), kmlPlacemark.getExtendedData("DeviceType"), kmlPlacemark.getExtendedData("Voltage"), jsonObject);
                        transformerSnippet.show();
                        if (isTracing) {
                            reSetColor();
                        }
                    }
                    mapView.invalidate();
                    return true;
                });

                if (caSectionList.contains(kmlPlacemark.getExtendedData("SectionId"))) {
                    int y = CaObject.getJSONArray("features").length();
                    for (int j = 0; j < y; j++) {
                        if (CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("properties").getString("SectionId").equals(kmlPlacemark.getExtendedData("SectionId")) && CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").length() > 2 && kmlPlacemark.getExtendedData("Location").equals("2")) {
                            fromY = String.valueOf(CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").length() - 2).get(0));
                            fromX = String.valueOf(CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").length() - 2).get(1));
                            marker.setRotation((float) ResponseDataUtils.CalculateAng(Double.parseDouble(fromX), Double.parseDouble(toX), Double.parseDouble(fromY), Double.parseDouble(toY)));
                        } else if (CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("properties").getString("SectionId").equals(kmlPlacemark.getExtendedData("SectionId")) && CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").length() > 2 && !kmlPlacemark.getExtendedData("Location").equals("2")) {
                            toY = String.valueOf(CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(1).get(0));
                            toX = String.valueOf(CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(1).get(1));
                            marker.setIcon(new BitmapDrawable(mapView.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(customizedBitmap, 0, 80)));
                            marker.setRotation((float) ResponseDataUtils.CalculateAng(Double.parseDouble(fromY), Double.parseDouble(toY), Double.parseDouble(fromX), Double.parseDouble(toX)));
                        }
                    }
                } else if (ohSectionList.contains(kmlPlacemark.getExtendedData("SectionId"))) {
                    int OhSize = OhObject.getJSONArray("features").length();
                    for (int k = 0; k < OhSize; k++) {
                        if (OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("properties").getString("SectionId").equals(kmlPlacemark.getExtendedData("SectionId")) && OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").length() > 2 && kmlPlacemark.getExtendedData("Location").equals("2")) {
                            fromY = String.valueOf(OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").length() - 2).get(0));
                            fromX = String.valueOf(OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").length() - 2).get(1));
                            marker.setRotation((float) ResponseDataUtils.CalculateAng(Double.parseDouble(fromY), Double.parseDouble(toY), Double.parseDouble(fromX), Double.parseDouble(toX)));
                        } else if (OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("properties").getString("SectionId").equals(kmlPlacemark.getExtendedData("SectionId")) && OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").length() > 2 && !kmlPlacemark.getExtendedData("Location").equals("2")) {
                            toX = String.valueOf(OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(1).get(1));
                            toY = String.valueOf(OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(1).get(0));
                            marker.setIcon(new BitmapDrawable(mapView.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(customizedBitmap, 0, 80)));
                            marker.setRotation((float) ResponseDataUtils.CalculateAng(Double.parseDouble(fromY), Double.parseDouble(toY), Double.parseDouble(fromX), Double.parseDouble(toX)));
                        }
                    }
                }

            } catch (Exception e) {
                Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
            }
        }

        @Override
        public void onLineString(Polyline polyline, KmlPlacemark kmlPlacemark, KmlLineString kmlLineString) {
        }

        @Override
        public void onPolygon(Polygon polygon, KmlPlacemark kmlPlacemark, KmlPolygon kmlPolygon) {
        }

        @Override
        public void onTrack(Polyline polyline, KmlPlacemark kmlPlacemark, KmlTrack kmlTrack) {
        }

        @Override
        public void onFeature(Overlay overlay, KmlFeature kmlFeature) {
        }
    }

    public class FuseKmlStyler implements KmlFeature.Styler {

        private final int mColor;
        private final MapView mapView;

        public FuseKmlStyler(int mColor, MapView mapView) {
            this.mColor = mColor;
            this.mapView = mapView;
        }

        @Override
        public void onPoint(Marker marker, KmlPlacemark kmlPlacemark, KmlPoint kmlPoint) {
            try {
                int i = kmlPlacemark.mGeometry.mCoordinates.size() - 1;
                GeoPoint geoPoint = new GeoPoint(kmlPoint.mCoordinates.get(i).getLatitude(), kmlPoint.mCoordinates.get(i).getLongitude());
                marker.setPosition(geoPoint);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                fuseSectionId.put(kmlPlacemark.getExtendedData("DeviceNumber"), marker);
                fuseList.add(marker);
                Bitmap FuseBitmap = null;
                if (kmlPlacemark.getExtendedData("ClosedPhase").equalsIgnoreCase("7")) {
                    FuseBitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "L", 90f, Color.BLACK);
                    int paddingTop = 0;
                    Bitmap output = Bitmap.createBitmap(FuseBitmap.getWidth(), FuseBitmap.getHeight() + paddingTop, Bitmap.Config.ARGB_8888);
                    Canvas canvas1 = new Canvas(output);
                    canvas1.drawBitmap(FuseBitmap, 0, paddingTop, null);
                    marker.setIcon(new BitmapDrawable(mapView.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(FuseBitmap, Color.BLACK), 0, 95)));
                } else {
                    FuseBitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "X", 90f, Color.BLACK);
                    int paddingLeft = 0; // Left padding in pixels
                    int paddingTop = 0; // Top padding in pixels
                    int paddingRight = 0; // Right padding in pixels
                    int paddingBottom = 20; // Bottom padding in pixels
                    int newWidth = FuseBitmap.getWidth() + paddingLeft + paddingRight;
                    int newHeight = FuseBitmap.getHeight() + paddingTop + paddingBottom;
                    Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, Objects.requireNonNull(FuseBitmap.getConfig()));
                    Canvas canva = new Canvas(paddedBitmap);
                    canva.drawBitmap(FuseBitmap, paddingLeft, paddingTop, null);
                    marker.setIcon(new BitmapDrawable(mapView.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.BLACK), 0, 95)));
                }

                marker.setRotation((float) ResponseDataUtils.CalculateAng(Double.parseDouble(kmlPlacemark.getExtendedData("FromNode_Y_l")), Double.parseDouble(kmlPlacemark.getExtendedData("ToNode_Y_l")), Double.parseDouble(kmlPlacemark.getExtendedData("FromNode_X_l")), Double.parseDouble(kmlPlacemark.getExtendedData("ToNode_X_l"))));

                marker.setOnMarkerClickListener((marker1, mapView) -> {
                    networkId = kmlPlacemark.getExtendedData("NetworkId");
                    nodeId = kmlPlacemark.getExtendedData("ToNodeId");

                    DelSectionId = kmlPlacemark.getExtendedData("SectionId");
                    DelDeviceNumber = kmlPlacemark.getExtendedData("DeviceNumber");
                    DelNetworkId = kmlPlacemark.getExtendedData("NetworkId");
                    DelDeviceType = "14";

                    if (Config.isLoadFlow) {
                        highlightLoadFlowDevice(marker1, "14", kmlPlacemark.getExtendedData("DeviceNumber"), Integer.parseInt(kmlPlacemark.getExtendedData("Location")));
                        LoadFlowBox loadFlowBox = new LoadFlowBox(mapView.getContext(), kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("DeviceType"), (ArrayList<String>) loadFlowList, kmlPlacemark.getExtendedData("EquipmentId"));
                        loadFragment(loadFlowBox, "loadFlowBoxTag");
                        if (isTracing) {
                            reSetColor();
                        }
                    } else if (Config.isShortCircuit) {
                        highlightShortCircuitDevice(marker1, "14", kmlPlacemark.getExtendedData("DeviceNumber"), Integer.parseInt(kmlPlacemark.getExtendedData("Location")));
                        ShortCircuitBox shortCircuitBox = new ShortCircuitBox(mapView.getContext(), (ArrayList<String>) shortCircuitList, kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("DeviceType"));
                        loadFragment(shortCircuitBox, "shortCircuitBoxTag");
                        if (isTracing) {
                            reSetColor();
                        }
                    } else {
                        highlightDevice(marker1, "14", kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("SectionId"), Integer.parseInt(kmlPlacemark.getExtendedData("Location")));
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("DeviceNumber", kmlPlacemark.getExtendedData("DeviceNumber"));
                        jsonObject.addProperty("DeviceType", kmlPlacemark.getExtendedData("DeviceType"));
                        FuseSnippet fuseSnippet = new FuseSnippet(MapActivity.this, kmlPlacemark.getExtendedData("SectionId"), kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("EquipmentId"), kmlPlacemark.getExtendedData("NetworkId"), kmlPlacemark.getExtendedData("DeviceType"), kmlPlacemark.getExtendedData("Voltage"), jsonObject);
                        fuseSnippet.show();
                        if (isTracing) {
                            reSetColor();
                        }
                    }
                    mapView.invalidate();
                    return true;
                });

                if (caSectionList.contains(kmlPlacemark.getExtendedData("SectionId"))) {
                    int CaSize = CaObject.getJSONArray("features").length();
                    for (int j = 0; j < CaSize; j++) {
                        if (CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("properties").getString("SectionId").equals(kmlPlacemark.getExtendedData("SectionId")) && CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").length() > 2 && kmlPlacemark.getExtendedData("Location").equals("2")) {
                            String fromY = String.valueOf(CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").length() - 2).get(0));
                            String fromX = String.valueOf(CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").length() - 2).get(1));
                            marker.setRotation((float) ResponseDataUtils.CalculateAng(Double.parseDouble(fromX), Double.parseDouble(kmlPlacemark.getExtendedData("ToNode_X_l")), Double.parseDouble(fromY), Double.parseDouble(kmlPlacemark.getExtendedData("ToNode_Y_l"))));
                        } else if (CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("properties").getString("SectionId").equals(kmlPlacemark.getExtendedData("SectionId")) && CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").length() > 2 && !kmlPlacemark.getExtendedData("Location").equals("2")) {
                            String toY = String.valueOf(CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(1).get(0));
                            String toX = String.valueOf(CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(1).get(1));
                            marker.setRotation((float) ResponseDataUtils.CalculateAng(Double.parseDouble(kmlPlacemark.getExtendedData("FromNode_X_l")), Double.parseDouble(toX), Double.parseDouble(kmlPlacemark.getExtendedData("FromNode_Y_l")), Double.parseDouble(toY)));
                        }
                    }
                } else if (ohSectionList.contains(kmlPlacemark.getExtendedData("SectionId"))) {
                    int OhSize = OhObject.getJSONArray("features").length();
                    for (int k = 0; k < OhSize; k++) {
                        if (OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("properties").getString("SectionId").equals(kmlPlacemark.getExtendedData("SectionId")) && OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").length() > 2 && kmlPlacemark.getExtendedData("Location").equals("2")) {
                            String fromY = String.valueOf(OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").length() - 2).get(0));
                            String fromX = String.valueOf(OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").length() - 2).get(1));
                            marker.setRotation((float) ResponseDataUtils.CalculateAng(Double.parseDouble(fromX), Double.parseDouble(kmlPlacemark.getExtendedData("ToNode_X_l")), Double.parseDouble(fromY), Double.parseDouble(kmlPlacemark.getExtendedData("ToNode_Y_l"))));
                        } else if (OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("properties").getString("SectionId").equals(kmlPlacemark.getExtendedData("SectionId")) && OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").length() > 2 && !kmlPlacemark.getExtendedData("Location").equals("2")) {
                            String toX = String.valueOf(OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(1).get(1));
                            String toY = String.valueOf(OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(1).get(0));
                            marker.setRotation((float) ResponseDataUtils.CalculateAng(Double.parseDouble(kmlPlacemark.getExtendedData("FromNode_X_l")), Double.parseDouble(toX), Double.parseDouble(kmlPlacemark.getExtendedData("FromNode_Y_l")), Double.parseDouble(toY)));
                        }
                    }
                }
            } catch (Exception e) {
                Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
            }
        }

        @Override
        public void onLineString(Polyline polyline, KmlPlacemark kmlPlacemark, KmlLineString kmlLineString) {

        }

        @Override
        public void onPolygon(Polygon polygon, KmlPlacemark kmlPlacemark, KmlPolygon kmlPolygon) {

        }

        @Override
        public void onTrack(Polyline polyline, KmlPlacemark kmlPlacemark, KmlTrack kmlTrack) {

        }

        @Override
        public void onFeature(Overlay overlay, KmlFeature kmlFeature) {

        }
    }

    public class SwitchKmlStyler implements KmlFeature.Styler {

        private final int mColor;
        private final MapView mapView;

        public SwitchKmlStyler(int mColor, MapView mapView) {
            this.mColor = mColor;
            this.mapView = mapView;
        }

        @Override
        public void onPoint(Marker marker, KmlPlacemark kmlPlacemark, KmlPoint kmlPoint) {
            try {
                int i = kmlPlacemark.mGeometry.mCoordinates.size() - 1;
                GeoPoint geoPoint = new GeoPoint(kmlPoint.mCoordinates.get(i).getLatitude(), kmlPoint.mCoordinates.get(i).getLongitude());
                marker.setPosition(geoPoint);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                switchSectionId.put(kmlPlacemark.getExtendedData("DeviceNumber"), marker);
                switchedList.add(marker);

                Bitmap SwitchBitmap = null;
                if (kmlPlacemark.getExtendedData("ClosedPhase").equalsIgnoreCase("7")) {
                    SwitchBitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "D", 70f, Color.BLACK);
                    int paddingLeft = 0; // Left padding in pixels
                    int paddingTop = 0; // Top padding in pixels
                    int paddingRight = 0; // Right padding in pixels
                    int paddingBottom = 12; // Bottom padding in pixels
                    int newWidth = SwitchBitmap.getWidth() + paddingLeft + paddingRight;
                    int newHeight = SwitchBitmap.getHeight() + paddingTop + paddingBottom;
                    Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, Objects.requireNonNull(SwitchBitmap.getConfig()));
                    Canvas canva = new Canvas(paddedBitmap);
                    canva.drawBitmap(SwitchBitmap, paddingLeft, paddingTop, null);
                    marker.setIcon(new BitmapDrawable(mapView.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.BLACK), 0, 95)));
                } else {
                    SwitchBitmap = OTFToBitmapConverter.convertOTFToBitmap(MapActivity.this, "N", 60f, Color.BLACK);
                    int paddingLeft = 0; // Left padding in pixels
                    int paddingTop = 0; // Top padding in pixels
                    int paddingRight = 0; // Right padding in pixels
                    int paddingBottom = 24; // Bottom padding in pixels
                    int newWidth = SwitchBitmap.getWidth() + paddingLeft + paddingRight;
                    int newHeight = SwitchBitmap.getHeight() + paddingTop + paddingBottom;
                    Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, SwitchBitmap.getConfig());
                    Canvas canva = new Canvas(paddedBitmap);
                    canva.drawBitmap(SwitchBitmap, paddingLeft, paddingTop, null);
                    marker.setIcon(new BitmapDrawable(mapView.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBitmapColor(paddedBitmap, Color.BLACK), 0, 90)));
                }

                marker.setRotation((float)
                        ResponseDataUtils.CalculateAng(Double.parseDouble
                                (kmlPlacemark.getExtendedData("FromNode_Y_l")), Double.parseDouble(kmlPlacemark.getExtendedData("ToNode_Y_l")), Double.parseDouble(kmlPlacemark.getExtendedData("FromNode_X_l")), Double.parseDouble(kmlPlacemark.getExtendedData("ToNode_X_l"))));

                marker.setOnMarkerClickListener((marker1, mapView) -> {
                    networkId = kmlPlacemark.getExtendedData("NetworkId");
                    nodeId = kmlPlacemark.getExtendedData("ToNodeId");

                    DelSectionId = kmlPlacemark.getExtendedData("SectionId");
                    DelDeviceNumber = kmlPlacemark.getExtendedData("DeviceNumber");
                    DelNetworkId = kmlPlacemark.getExtendedData("NetworkId");
                    DelDeviceType = "13";

                    if (Config.isLoadFlow) {
                        highlightLoadFlowDevice(marker1, "13", kmlPlacemark.getExtendedData("DeviceNumber"), Integer.parseInt(kmlPlacemark.getExtendedData("Location")));
                        LoadFlowBox loadFlowBox = new LoadFlowBox(mapView.getContext(), kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("DeviceType"), (ArrayList<String>) loadFlowList, kmlPlacemark.getExtendedData("EquipmentId"));
                        loadFragment(loadFlowBox, "loadFlowBoxTag");
                        if (isTracing) {
                            reSetColor();
                        }
                    } else if (Config.isShortCircuit) {
                        highlightShortCircuitDevice(marker1, "13", kmlPlacemark.getExtendedData("DeviceNumber"), Integer.parseInt(kmlPlacemark.getExtendedData("Location")));
                        ShortCircuitBox shortCircuitBox = new ShortCircuitBox(mapView.getContext(), (ArrayList<String>) shortCircuitList, kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("DeviceType"));
                        loadFragment(shortCircuitBox, "shortCircuitBoxTag");
                        if (isTracing) {
                            reSetColor();
                        }
                    } else {
                        highlightDevice(marker1, "13", kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("SectionId"), Integer.parseInt(kmlPlacemark.getExtendedData("Location")));
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("DeviceNumber", kmlPlacemark.getExtendedData("DeviceNumber"));
                        jsonObject.addProperty("DeviceType", kmlPlacemark.getExtendedData("DeviceType"));
                        SwitchSnippet switchSnippet = new SwitchSnippet(MapActivity.this, kmlPlacemark.getExtendedData("SectionId"), kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("EquipmentId"), kmlPlacemark.getExtendedData("NetworkId"), kmlPlacemark.getExtendedData("DeviceType"), kmlPlacemark.getExtendedData("Voltage"), jsonObject);
                        switchSnippet.show();
                        if (isTracing) {
                            reSetColor();
                        }
                    }
                    mapView.invalidate();
                    return true;
                });

                if (caSectionList.contains(kmlPlacemark.getExtendedData("SectionId"))) {
                    int CaSize = CaObject.getJSONArray("features").length();
                    for (int j = 0; j < CaSize; j++) {
                        if (CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("properties").getString("SectionId").equals(kmlPlacemark.getExtendedData("SectionId")) && CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").length() > 2 && kmlPlacemark.getExtendedData("Location").equals("2")) {
                            String fromY = String.valueOf(CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").length() - 2).get(0));
                            String fromX = String.valueOf(CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").length() - 2).get(1));
                            marker.setRotation((float) ResponseDataUtils.CalculateAng(Double.parseDouble(fromY), Double.parseDouble(fromX), Double.parseDouble(kmlPlacemark.getExtendedData("ToNode_Y_l")), Double.parseDouble(kmlPlacemark.getExtendedData("ToNode_X_l"))));
                            Log.d("SwitchCableFrom", kmlPlacemark.getExtendedData("DeviceNumber"));
                        } else if (CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("properties").getString("SectionId").equals(kmlPlacemark.getExtendedData("SectionId")) && CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").length() > 2 && !kmlPlacemark.getExtendedData("Location").equals("2")) {
                            String toY = String.valueOf(CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(1).get(0));
                            String toX = String.valueOf(CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(1).get(1));
                            marker.setIcon(new BitmapDrawable(mapView.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(SwitchBitmap, 0, 80)));
                            marker.setRotation((float) ResponseDataUtils.CalculateAng(Double.parseDouble(kmlPlacemark.getExtendedData("FromNode_Y_l")), Double.parseDouble(kmlPlacemark.getExtendedData("FromNode_X_l")), Double.parseDouble(toY), Double.parseDouble(toX)));
                            Log.d("SwitchCableTo", kmlPlacemark.getExtendedData("DeviceNumber"));
                        }
                    }
                } else if (ohSectionList.contains(kmlPlacemark.getExtendedData("SectionId"))) {
                    int OhSize = OhObject.getJSONArray("features").length();
                    for (int k = 0; k < OhSize; k++) {
                        if (OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("properties").getString("SectionId").equals(kmlPlacemark.getExtendedData("SectionId")) && OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").length() > 2 && kmlPlacemark.getExtendedData("Location").equals("2")) {
                            String fromY = String.valueOf(OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").length() - 2).get(0));
                            String fromX = String.valueOf(OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").length() - 2).get(1));
                            marker.setRotation((float) ResponseDataUtils.CalculateAng(Double.parseDouble(fromY), Double.parseDouble(fromX), Double.parseDouble(kmlPlacemark.getExtendedData("ToNode_Y_l")), Double.parseDouble(kmlPlacemark.getExtendedData("ToNode_X_l"))));
                            Log.d("SwitchOverheadFrom", kmlPlacemark.getExtendedData("DeviceNumber"));
                        } else if (OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("properties").getString("SectionId").equals(kmlPlacemark.getExtendedData("SectionId")) && OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").length() > 2 && !kmlPlacemark.getExtendedData("Location").equals("2")) {
                            String toX = String.valueOf(OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(1).get(1));
                            String toY = String.valueOf(OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(1).get(0));
                            marker.setRotation((float) ResponseDataUtils.CalculateAng(Double.parseDouble(kmlPlacemark.getExtendedData("FromNode_Y_l")), Double.parseDouble(kmlPlacemark.getExtendedData("FromNode_X_l")), Double.parseDouble(toY), Double.parseDouble(toX)));
                            Log.d("SwitchOverheadTo", kmlPlacemark.getExtendedData("DeviceNumber"));
                        }
                    }
                }

            } catch (Exception e) {
                Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
            }
        }

        @Override
        public void onLineString(Polyline polyline, KmlPlacemark kmlPlacemark, KmlLineString kmlLineString) {

        }

        @Override
        public void onPolygon(Polygon polygon, KmlPlacemark kmlPlacemark, KmlPolygon kmlPolygon) {

        }

        @Override
        public void onTrack(Polyline polyline, KmlPlacemark kmlPlacemark, KmlTrack kmlTrack) {

        }

        @Override
        public void onFeature(Overlay overlay, KmlFeature kmlFeature) {

        }
    }

    public class ShuntCapacitorKmlStyler implements KmlFeature.Styler {

        private final int mColor;
        private final MapView mapView;

        public ShuntCapacitorKmlStyler(int mColor, MapView mapView) {
            this.mColor = mColor;
            this.mapView = mapView;
        }

        @Override
        public void onPoint(Marker marker, KmlPlacemark kmlPlacemark, KmlPoint kmlPoint) {
            try {
                int i = kmlPlacemark.mGeometry.mCoordinates.size() - 1;
                GeoPoint geoPoint = new GeoPoint(kmlPoint.mCoordinates.get(i).getLatitude(), kmlPoint.mCoordinates.get(i).getLongitude());
                marker.setPosition(geoPoint);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                capacitorSectionId.put(kmlPlacemark.getExtendedData("DeviceNumber"), marker);
                capacitorList.add(marker);

                if (spLineSectionList.contains(kmlPlacemark.getExtendedData("DeviceNumber"))) {
                    marker.setRotation((float) ResponseDataUtils.CalculateAng(Double.parseDouble(kmlPlacemark.getExtendedData("FromNode_X_l")), Double.parseDouble(kmlPlacemark.getExtendedData("ToNode_X_l")), Double.parseDouble(kmlPlacemark.getExtendedData("FromNode_Y_l")), Double.parseDouble(kmlPlacemark.getExtendedData("ToNode_Y_l"))));
                    marker.setIcon(new BitmapDrawable(mapView.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.BLACK), 0, 0)));
                } else {
                    marker.setRotation((float) ResponseDataUtils.CalculateAng(Double.parseDouble(kmlPlacemark.getExtendedData("FromNode_X_l")), Double.parseDouble(kmlPlacemark.getExtendedData("ToNode_Y_l")), Double.parseDouble(kmlPlacemark.getExtendedData("ToNode_X_l")), Double.parseDouble(kmlPlacemark.getExtendedData("FromNode_Y_l"))));
                    marker.setIcon(new BitmapDrawable(mapView.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Shuntcapacitor(), Color.BLACK), 0, 0)));
                }

                marker.setOnMarkerClickListener((marker1, mapView) -> {
                    networkId = kmlPlacemark.getExtendedData("NetworkId");
                    nodeId = kmlPlacemark.getExtendedData("ToNodeId");

                    DelSectionId = kmlPlacemark.getExtendedData("SectionId");
                    DelDeviceNumber = kmlPlacemark.getExtendedData("DeviceNumber");
                    DelNetworkId = kmlPlacemark.getExtendedData("NetworkId");
                    DelDeviceType = "17";

                    if (Config.isLoadFlow) {
                        highlightLoadFlowDevice(marker1, "17", kmlPlacemark.getExtendedData("DeviceNumber"), Integer.parseInt(kmlPlacemark.getExtendedData("Location")));
                        LoadFlowBox loadFlowBox = new LoadFlowBox(mapView.getContext(), kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("DeviceType"), (ArrayList<String>) loadFlowList, kmlPlacemark.getExtendedData("EquipmentId"));
                        loadFragment(loadFlowBox, "loadFlowBoxTag");
                        if (isTracing) {
                            reSetColor();
                        }
                    } else if (Config.isShortCircuit) {
                        highlightShortCircuitDevice(marker1, "17", kmlPlacemark.getExtendedData("DeviceNumber"), Integer.parseInt(kmlPlacemark.getExtendedData("Location")));
                        ShortCircuitBox shortCircuitBox = new ShortCircuitBox(mapView.getContext(), (ArrayList<String>) shortCircuitList, kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("DeviceType"));
                        loadFragment(shortCircuitBox, "shortCircuitBoxTag");
                        if (isTracing) {
                            reSetColor();
                        }
                    } else {
                        highlightDevice(marker1, "17", kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("SectionId"), Integer.parseInt(kmlPlacemark.getExtendedData("Location")));
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("DeviceNumber", kmlPlacemark.getExtendedData("DeviceNumber"));
                        jsonObject.addProperty("DeviceType", kmlPlacemark.getExtendedData("DeviceType"));
                        ShuntCapacitorSnippet shuntCapacitorSnippet = new ShuntCapacitorSnippet(MapActivity.this, kmlPlacemark.getExtendedData("SectionId"), kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("EquipmentId"), kmlPlacemark.getExtendedData("NetworkId"), kmlPlacemark.getExtendedData("DeviceType"), jsonObject);
                        shuntCapacitorSnippet.show();
                        if (isTracing) {
                            reSetColor();
                        }
                    }
                    mapView.invalidate();
                    return true;
                });

                if (caSectionList.contains(kmlPlacemark.getExtendedData("SectionId"))) {
                    int CaSize = CaObject.getJSONArray("features").length();
                    for (int j = 0; j < CaSize; j++) {
                        if (CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("properties").getString("SectionId").equals(kmlPlacemark.getExtendedData("SectionId")) && CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").length() > 2 && kmlPlacemark.getExtendedData("Location").equals("2")) {
                            String fromY = String.valueOf(CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").length() - 2).get(0));
                            String fromX = String.valueOf(CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").length() - 2).get(1));
                            marker.setRotation((float) ResponseDataUtils.CalculateAng(Double.parseDouble(fromX), Double.parseDouble(kmlPlacemark.getExtendedData("ToNode_X_l")), Double.parseDouble(fromY), Double.parseDouble(kmlPlacemark.getExtendedData("ToNode_Y_l"))));
                        } else if (CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("properties").getString("SectionId").equals(kmlPlacemark.getExtendedData("SectionId")) && CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").length() > 2 && !kmlPlacemark.getExtendedData("Location").equals("2")) {
                            String toY = String.valueOf(CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(1).get(0));
                            String toX = String.valueOf(CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(1).get(1));
                            marker.setRotation((float) ResponseDataUtils.CalculateAng(Double.parseDouble(kmlPlacemark.getExtendedData("FromNode_X_l")), Double.parseDouble(toX), Double.parseDouble(kmlPlacemark.getExtendedData("FromNode_Y_l")), Double.parseDouble(toY)));
                        }
                    }
                } else if (ohSectionList.contains(kmlPlacemark.getExtendedData("SectionId"))) {
                    int OhSize = OhObject.getJSONArray("features").length();
                    for (int k = 0; k < OhSize; k++) {
                        if (OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("properties").getString("SectionId").equals(kmlPlacemark.getExtendedData("SectionId")) && OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").length() > 2 && kmlPlacemark.getExtendedData("Location").equals("2")) {
                            String fromY = String.valueOf(OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").length() - 2).get(0));
                            String fromX = String.valueOf(OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").length() - 2).get(1));
                            marker.setRotation((float) ResponseDataUtils.CalculateAng(Double.parseDouble(fromX), Double.parseDouble(kmlPlacemark.getExtendedData("ToNode_X_l")), Double.parseDouble(fromY), Double.parseDouble(kmlPlacemark.getExtendedData("ToNode_Y_l"))));
                        } else if (OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("properties").getString("SectionId").equals(kmlPlacemark.getExtendedData("SectionId")) && OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").length() > 2 && !kmlPlacemark.getExtendedData("Location").equals("2")) {
                            String toX = String.valueOf(OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(1).get(1));
                            String toY = String.valueOf(OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(1).get(0));
                            marker.setRotation((float) ResponseDataUtils.CalculateAng(Double.parseDouble(kmlPlacemark.getExtendedData("FromNode_X_l")), Double.parseDouble(toX), Double.parseDouble(kmlPlacemark.getExtendedData("FromNode_Y_l")), Double.parseDouble(toY)));
                        }
                    }
                }

            } catch (Exception e) {
                Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
            }
        }

        @Override
        public void onLineString(Polyline polyline, KmlPlacemark kmlPlacemark, KmlLineString kmlLineString) {

        }

        @Override
        public void onPolygon(Polygon polygon, KmlPlacemark kmlPlacemark, KmlPolygon kmlPolygon) {
        }

        @Override
        public void onTrack(Polyline polyline, KmlPlacemark kmlPlacemark, KmlTrack kmlTrack) {
        }

        @Override
        public void onFeature(Overlay overlay, KmlFeature kmlFeature) {
        }

    }

    public class SpotLoadKmlStyler implements KmlFeature.Styler {

        private final int mColor;
        private final MapView mapView;

        public SpotLoadKmlStyler(int mColor, MapView mapView) {
            this.mColor = mColor;
            this.mapView = mapView;
        }

        @Override
        public void onPoint(Marker marker, KmlPlacemark kmlPlacemark, KmlPoint kmlPoint) {
            try {
                int i = kmlPlacemark.mGeometry.mCoordinates.size() - 1;
                GeoPoint geoPoint = new GeoPoint(kmlPoint.mCoordinates.get(i).getLatitude(), kmlPoint.mCoordinates.get(i).getLongitude());
                marker.setPosition(geoPoint);
                spotLoadSectionId.put(kmlPlacemark.getExtendedData("DeviceNumber"), marker);
                spotLoadList.add(marker);

                int paddingPx = 0;
                Bitmap paddedBitmap = Bitmap.createBitmap(BitmapImg.Spotload().getWidth() + paddingPx * 2,
                        BitmapImg.Spotload().getHeight() + paddingPx * 2, Bitmap.Config.ARGB_8888);
                Canvas canvas1 = new Canvas(paddedBitmap);
                canvas1.drawBitmap(BitmapImg.Spotload(), paddingPx, 10, null);

                if (spLineSectionList.contains(kmlPlacemark.getExtendedData("DeviceNumber"))) {
                    marker.setIcon(new BitmapDrawable(mapView.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(paddedBitmap, Color.BLACK), 0, 0)));
                    marker.setRotation((float) ResponseDataUtils.calculateAngles(Double.parseDouble(kmlPlacemark.getExtendedData("ToNode_X_l")), Double.parseDouble(kmlPlacemark.getExtendedData("ToNode_Y_l")), Double.parseDouble(kmlPlacemark.getExtendedData("FromNode_X_l")), Double.parseDouble(kmlPlacemark.getExtendedData("FromNode_Y_l"))) + 90);
                } else {
                    marker.setIcon(new BitmapDrawable(mapView.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Spotload(), Color.BLACK), 0, 70)));
                    marker.setRotation((float) ResponseDataUtils.calculateAngles(Double.parseDouble(kmlPlacemark.getExtendedData("FromNode_X_l")), Double.parseDouble(kmlPlacemark.getExtendedData("FromNode_Y_l")), Double.parseDouble(kmlPlacemark.getExtendedData("ToNode_X_l")), Double.parseDouble(kmlPlacemark.getExtendedData("ToNode_Y_l"))));
                }

                marker.setOnMarkerClickListener((marker1, mapView) -> {
                    networkId = kmlPlacemark.getExtendedData("NetworkId");
                    nodeId = kmlPlacemark.getExtendedData("ToNodeId");

                    DelSectionId = kmlPlacemark.getExtendedData("SectionId");
                    DelDeviceNumber = kmlPlacemark.getExtendedData("DeviceNumber");
                    DelNetworkId = kmlPlacemark.getExtendedData("NetworkId");
                    DelDeviceType = "20";

                    if (Config.isLoadFlow) {
                        highlightLoadFlowDevice(marker1, "20", kmlPlacemark.getExtendedData("DeviceNumber"), Integer.parseInt(kmlPlacemark.getExtendedData("Location")));
                        LoadFlowBox loadFlowBox = new LoadFlowBox(mapView.getContext(), kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("DeviceType"), (ArrayList<String>) loadFlowList, "");
                        loadFragment(loadFlowBox, "loadFlowBoxTag");
                        if (isTracing) {
                            reSetColor();
                        }
                    } else if (Config.isShortCircuit) {
                        highlightShortCircuitDevice(marker1, "20", kmlPlacemark.getExtendedData("DeviceNumber"), Integer.parseInt(kmlPlacemark.getExtendedData("Location")));
                        ShortCircuitBox shortCircuitBox = new ShortCircuitBox(mapView.getContext(), (ArrayList<String>) shortCircuitList, kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("DeviceType"));
                        loadFragment(shortCircuitBox, "shortCircuitBoxTag");
                        if (isTracing) {
                            reSetColor();
                        }
                    } else {
                        highlightDevice(marker1, "20", kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("SectionId"), Integer.parseInt(kmlPlacemark.getExtendedData("Location")));
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("DeviceNumber", kmlPlacemark.getExtendedData("DeviceNumber"));
                        jsonObject.addProperty("DeviceType", kmlPlacemark.getExtendedData("DeviceType"));
                        SpotLoadSnippet spotLoadSnippet = new SpotLoadSnippet(MapActivity.this, kmlPlacemark.getExtendedData("DeviceNumber"), kmlPlacemark.getExtendedData("NetworkId"), kmlPlacemark.getExtendedData("ConnectedKVA"), kmlPlacemark.getExtendedData("Customers"), kmlPlacemark.getExtendedData("DeviceType"), jsonObject);
                        spotLoadSnippet.show();
                        if (isTracing) {
                            reSetColor();
                        }
                    }
                    mapView.invalidate();
                    return true;
                });

                if (caSectionList.contains(kmlPlacemark.getExtendedData("SectionId"))) {
                    int CaSize = CaObject.getJSONArray("features").length();
                    for (int j = 0; j < CaSize; j++) {
                        if (CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("properties").getString("SectionId").equals(kmlPlacemark.getExtendedData("SectionId")) && CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").length() > 2 && kmlPlacemark.getExtendedData("Location").equals("2")) {
                            String fromY = String.valueOf(CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").length() - 2).get(0));
                            String fromX = String.valueOf(CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").length() - 2).get(1));
                            marker.setIcon(new BitmapDrawable(mapView.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Spotload(), Color.BLACK), 0, 70)));
                            marker.setRotation((float) ResponseDataUtils.calculateAngles(Double.parseDouble(fromX), Double.parseDouble(fromY), Double.parseDouble(kmlPlacemark.getExtendedData("ToNode_X_l")), Double.parseDouble(kmlPlacemark.getExtendedData("ToNode_Y_l")))); // -90
                        } else if (CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("properties").getString("SectionId").equals(kmlPlacemark.getExtendedData("SectionId")) && CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").length() > 2 && !kmlPlacemark.getExtendedData("Location").equals("2")) {
                            String toY = String.valueOf(CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(1).get(0));
                            String toX = String.valueOf(CaObject.getJSONArray("features").getJSONObject(j).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(1).get(1));
                            marker.setIcon(new BitmapDrawable(mapView.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Spotload(), Color.BLACK), 0, 70)));
                            marker.setRotation((float) ResponseDataUtils.calculateAngles(Double.parseDouble(kmlPlacemark.getExtendedData("FromNode_X_l")), Double.parseDouble(kmlPlacemark.getExtendedData("FromNode_Y_l")), Double.parseDouble(toX), Double.parseDouble(toY)));
                        }
                    }
                } else if (ohSectionList.contains(kmlPlacemark.getExtendedData("SectionId"))) {
                    int OhSize = OhObject.getJSONArray("features").length();
                    for (int k = 0; k < OhSize; k++) {
                        if (OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("properties").getString("SectionId").equals(kmlPlacemark.getExtendedData("SectionId")) && OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").length() > 2 && kmlPlacemark.getExtendedData("Location").equals("2")) {
                            String fromY = String.valueOf(OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").length() - 2).get(0));
                            String fromX = String.valueOf(OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").length() - 2).get(1));
                            marker.setIcon(new BitmapDrawable(mapView.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Spotload(), Color.BLACK), 0, 70)));
                            marker.setRotation((float) ResponseDataUtils.calculateAngles(Double.parseDouble(fromX), Double.parseDouble(fromY), Double.parseDouble(kmlPlacemark.getExtendedData("ToNode_X_l")), Double.parseDouble(kmlPlacemark.getExtendedData("ToNode_Y_l"))));
                        } else if (OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("properties").getString("SectionId").equals(kmlPlacemark.getExtendedData("SectionId")) && OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").length() > 2 && !kmlPlacemark.getExtendedData("Location").equals("2")) {
                            String toX = String.valueOf(OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(1).get(1));
                            String toY = String.valueOf(OhObject.getJSONArray("features").getJSONObject(k).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(1).get(0));
                            marker.setIcon(new BitmapDrawable(mapView.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(changeBgTransparentBitmapColor(BitmapImg.Spotload(), Color.BLACK), 0, 70)));
                            marker.setRotation((float) ResponseDataUtils.calculateAngles(Double.parseDouble(kmlPlacemark.getExtendedData("FromNode_X_l")), Double.parseDouble(kmlPlacemark.getExtendedData("FromNode_Y_l")), Double.parseDouble(toX), Double.parseDouble(toY)));
                        }
                    }
                }

            } catch (Exception e) {
                Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
            }
        }

        @Override
        public void onLineString(Polyline polyline, KmlPlacemark kmlPlacemark, KmlLineString kmlLineString) {

        }

        @Override
        public void onPolygon(Polygon polygon, KmlPlacemark kmlPlacemark, KmlPolygon kmlPolygon) {

        }

        @Override
        public void onTrack(Polyline polyline, KmlPlacemark kmlPlacemark, KmlTrack kmlTrack) {

        }

        @Override
        public void onFeature(Overlay overlay, KmlFeature kmlFeature) {

        }
    }

    public class SourceKmlStyler implements KmlFeature.Styler {

        private final int mColor;
        private final MapView mapView;

        public SourceKmlStyler(int mColor, MapView mapView) {
            this.mColor = mColor;
            this.mapView = mapView;
        }

        @Override
        public void onFeature(Overlay overlay, KmlFeature kmlFeature) {

        }

        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public void onPoint(Marker marker, KmlPlacemark kmlPlacemark, KmlPoint kmlPoint) {
            try {
               /* int i = kmlPlacemark.mGeometry.mCoordinates.size() - 1;
                GeoPoint geoPoint = new GeoPoint(kmlPoint.mCoordinates.get(i).getLatitude(), kmlPoint.mCoordinates.get(i).getLongitude());
                marker.setPosition(geoPoint);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                Drawable drawable = getResources().getDrawable(R.drawable.source);
                Bitmap bites = drawableToBitmap(drawable);
                Bitmap customizedBitmap = changeSourceColor(bites, Color.BLACK);
                marker.setIcon(new BitmapDrawable(mapView.getContext().getResources(), ResponseDataUtils.addPaddingToBitmap(customizedBitmap, 0, 0)));*/

                marker.setIcon(getDrawable(R.drawable.location));

                marker.setOnMarkerClickListener((marker1, mapView1) -> {

                    if (selectedNodeID != null) {
                        GeoPoint geoPoint1 = new GeoPoint(
                                Double.parseDouble(kmlPlacemark.getExtendedData("fromy")), Double.parseDouble(kmlPlacemark.getExtendedData("fromX"))
                        );

                        if (!newSectionGeoPointList.contains(geoPoint1)) {
                            newSectionGeoPointList.add(geoPoint1);
                            coordinateList.add(geoPoint1);
                        }
                    } else {
                        selectedNodeID = kmlPlacemark.getExtendedData("NodeId");

                        GeoPoint geoPoint1 = new GeoPoint(
                                Double.parseDouble(kmlPlacemark.getExtendedData("fromy")), Double.parseDouble(kmlPlacemark.getExtendedData("fromX"))
                        );

                        if (!newSectionGeoPointList.contains(geoPoint1)) {
                            newSectionGeoPointList.add(geoPoint1);
                            coordinateList.add(geoPoint1);
                        }

                    }

                    SourceDialog sourceDialog = new SourceDialog(MapActivity.this, getSupportFragmentManager(), getLifecycle(), kmlPlacemark.getExtendedData("NodeId"), kmlPlacemark.getExtendedData("fromX"), kmlPlacemark.getExtendedData("fromy"), kmlPlacemark.getExtendedData("UTMX"), kmlPlacemark.getExtendedData("UTMY"));
                    sourceDialog.show();
                    return true;
                });

            } catch (Exception e) {
                Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
            }
        }

        @Override
        public void onLineString(Polyline polyline, KmlPlacemark kmlPlacemark, KmlLineString kmlLineString) {
        }

        @Override
        public void onPolygon(Polygon polygon, KmlPlacemark kmlPlacemark, KmlPolygon kmlPolygon) {

        }

        @Override
        public void onTrack(Polyline polyline, KmlPlacemark kmlPlacemark, KmlTrack kmlTrack) {

        }
    }

    public class NodeKmlStyler implements KmlFeature.Styler {

        private final int mColor;
        private final MapView mapView;

        public NodeKmlStyler(int mColor, MapView mapView) {
            this.mColor = mColor;
            this.mapView = mapView;
        }

        @Override
        public void onPoint(Marker marker, KmlPlacemark kmlPlacemark, KmlPoint kmlPoint) {
            try {
                int i = kmlPlacemark.mGeometry.mCoordinates.size() - 1;
                GeoPoint geoPoint = new GeoPoint(kmlPoint.mCoordinates.get(i).getLatitude(), kmlPoint.mCoordinates.get(i).getLongitude());
                marker.setPosition(geoPoint);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                marker.setDraggable(false);

                marker.setVisible(true);
                marker.setEnabled(true);
                marker.setAlpha(7.5f);

                @SuppressLint("UseCompatLoadingForDrawables")
                Drawable drawable = getResources().getDrawable(R.drawable.dot);
                Bitmap bitmap = drawableToBitmap(drawable);
                marker.setIcon(new BitmapDrawable(mapView.getContext().getResources(), bitmap));

                NodeId.put(kmlPlacemark.getExtendedData("NodeId"), marker);

                marker.setOnMarkerClickListener((marker1, mapView) -> {
                    networkId = kmlPlacemark.getExtendedData("NetworkId");
                    nodeId = kmlPlacemark.getExtendedData("NodeId");

                    if (selectedNodeID != null) {
                        GeoPoint geoPoint1 = new GeoPoint(
                                Double.parseDouble(kmlPlacemark.getExtendedData("fromy")), Double.parseDouble(kmlPlacemark.getExtendedData("fromX"))
                        );

                        if (!newSectionGeoPointList.contains(geoPoint1)) {
                            newSectionGeoPointList.add(geoPoint1);
                            coordinateList.add(geoPoint1);
                        }

                    } else {
                        selectedNodeID = kmlPlacemark.getExtendedData("NodeId");

                        GeoPoint geoPoint1 = new GeoPoint(
                                Double.parseDouble(kmlPlacemark.getExtendedData("fromy")), Double.parseDouble(kmlPlacemark.getExtendedData("fromX"))
                        );

                        if (!newSectionGeoPointList.contains(geoPoint1)) {
                            newSectionGeoPointList.add(geoPoint1);
                            coordinateList.add(geoPoint1);
                        }

                    }

                    if (Objects.requireNonNull(intent.getStringExtra("Type")).contains("Normal")) {
                        NodeSelected nodeSelected = new NodeSelected(mapView.getContext(), kmlPlacemark.getExtendedData("NodeId"), marker1);
                        nodeSelected.show();
                    }

                    if (Config.isLoadFlow) {
                        highlightLoadFlowDevice(marker1, "99", kmlPlacemark.getExtendedData("NodeId"), 0);
                        LoadFlowBox loadFlowBox = new LoadFlowBox(mapView.getContext(), kmlPlacemark.getExtendedData("NodeId"), "41", (ArrayList<String>) loadFlowList, kmlPlacemark.getExtendedData("NodeId"));
                        loadFragment(loadFlowBox, "loadFlowBoxTag");
                        if (isTracing) {
                            reSetColor();
                        }
                    } else if (Config.isShortCircuit) {
                        highlightShortCircuitDevice(marker1, "99", kmlPlacemark.getExtendedData("NodeId"), 0);
                        ShortCircuitBox shortCircuitBox = new ShortCircuitBox(mapView.getContext(), (ArrayList<String>) shortCircuitList, kmlPlacemark.getExtendedData("NodeId"), "41");
                        loadFragment(shortCircuitBox, "shortCircuitBoxTag");
                        if (isTracing) {
                            reSetColor();
                        }
                    } else {
                        highlightDevice(marker1, "99", kmlPlacemark.getExtendedData("NodeId"), kmlPlacemark.getExtendedData("NodeId"), 0);
                        selectedNode = marker1;
                        if (isTracing) {
                            reSetColor();
                        }
                    }

                    mapView.invalidate();
                    return true;
                });

                marker.setOnMarkerDragListener(new Marker.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDrag(Marker marker) {

                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {

                    }

                    @Override
                    public void onMarkerDragStart(Marker marker) {

                    }
                });

            } catch (Exception e) {
                Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
            }
        }

        @Override
        public void onLineString(Polyline polyline, KmlPlacemark kmlPlacemark, KmlLineString kmlLineString) {

        }

        @Override
        public void onPolygon(Polygon polygon, KmlPlacemark kmlPlacemark, KmlPolygon kmlPolygon) {

        }

        @Override
        public void onTrack(Polyline polyline, KmlPlacemark kmlPlacemark, KmlTrack kmlTrack) {

        }

        @Override
        public void onFeature(Overlay overlay, KmlFeature kmlFeature) {

        }

    }

    private class NodeSelected extends Dialog {

        private final Context context;
        private final String nodeID;
        private final Marker marker;
        private NodePopLayoutBinding binding;

        public NodeSelected(@NonNull Context context, String nodeId, Marker marker) {
            super(context);
            this.context = context;
            this.nodeID = nodeId;
            this.marker = marker;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            binding = NodePopLayoutBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            View MainLayoutBackGround = Objects.requireNonNull(getWindow()).getDecorView().getRootView();
            MainLayoutBackGround.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            MainLayoutBackGround.setBackgroundResource(R.drawable.pop_background);

            binding.btnConventional.setOnClickListener(v -> {
                shortCircuit = new ShortCircuit(MapActivity.this);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("Network", intent.getStringArrayListExtra("NetworkId"));
                bundle.putString("Index", "1");
                bundle.putString("NodeId", nodeID);
                shortCircuit.setArguments(bundle);
                shortCircuit.show(getSupportFragmentManager(), shortCircuit.getTag());
                @SuppressLint("UseCompatLoadingForDrawables")
                Drawable drawable = getResources().getDrawable(R.drawable.dot);
                Bitmap bitmap = drawableToBitmap(drawable);
                marker.setIcon(new BitmapDrawable(context.getResources(), bitmap));
                dismiss();
            });

            binding.cancelConvention.setOnClickListener(v -> dismiss());
        }

        @Override
        public void dismiss() {
            super.dismiss();
            binding = null;
        }
    }

}